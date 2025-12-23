package com.example.cmdserver.service;

import com.example.cmdserver.config.CommandProperties;
import com.example.cmdserver.dto.CommandRequest;
import com.example.cmdserver.dto.CommandResponse;
import com.example.cmdserver.model.CommandStatus;
import com.example.cmdserver.queue.CommandQueueFullException;
import com.example.cmdserver.queue.CommandTask;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommandQueueService {

    private static final Logger log = LoggerFactory.getLogger(CommandQueueService.class);

    private final BlockingQueue<CommandTask> queue;
    private final ListeningExecutorService consumerExecutor;
    private final CommandDispatchService dispatchService;
    private final CommandProperties properties;

    public CommandQueueService(BlockingQueue<CommandTask> queue,
                               ListeningExecutorService commandConsumerExecutor,
                               CommandDispatchService dispatchService,
                               CommandProperties properties) {
        this.queue = queue;
        this.consumerExecutor = commandConsumerExecutor;
        this.dispatchService = dispatchService;
        this.properties = properties;
        startConsumers();
    }

    public ListenableFuture<CommandResponse> enqueue(CommandRequest request) {
        Objects.requireNonNull(request, "command request must not be null");
        var commandId = UUID.randomUUID().toString();
        var acceptedAt = Instant.now();
        var responseFuture = SettableFuture.<CommandResponse>create();
        var task = new CommandTask(commandId, request, acceptedAt, responseFuture);

        try {
            var timeout = properties.getQueue().getOfferTimeout();
            boolean offered = queue.offer(task, timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!offered) {
                throw new CommandQueueFullException("Command queue is full; please retry later");
            }
            log.debug("Command {} enqueued for device {}", commandId, request.deviceId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            responseFuture.setException(e);
        }

        return responseFuture;
    }

    private void startConsumers() {
        int workers = properties.getQueue().getConsumerThreads();
        for (int i = 0; i < workers; i++) {
            consumerExecutor.submit(this::consumeLoop);
        }
    }

    private void consumeLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                CommandTask task = queue.take();
                log.debug("Dequeued command {} for device {}", task.getCommandId(), task.getRequest().deviceId());
                var dispatchFuture = dispatchService.dispatch(task);
                attachCallbacks(task, dispatchFuture);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void attachCallbacks(CommandTask task, ListenableFuture<CommandResponse> dispatchFuture) {
        Futures.addCallback(dispatchFuture, new FutureCallback<>() {
            @Override
            public void onSuccess(CommandResponse result) {
                task.getResultFuture().set(result);
            }

            @Override
            public void onFailure(Throwable t) {
                task.getResultFuture().setException(t);
            }
        }, MoreExecutors.directExecutor());
    }
}
