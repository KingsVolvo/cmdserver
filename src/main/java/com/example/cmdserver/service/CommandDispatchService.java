package com.example.cmdserver.service;

import com.example.cmdserver.config.CommandProperties;
import com.example.cmdserver.dto.CommandResponse;
import com.example.cmdserver.model.CommandStatus;
import com.example.cmdserver.queue.CommandTask;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommandDispatchService {

    private static final Logger log = LoggerFactory.getLogger(CommandDispatchService.class);

    private final ListeningExecutorService deviceDispatchExecutor;
    private final CommandProperties properties;

    public CommandDispatchService(ListeningExecutorService deviceDispatchExecutor, CommandProperties properties) {
        this.deviceDispatchExecutor = deviceDispatchExecutor;
        this.properties = properties;
    }

    public ListenableFuture<CommandResponse> dispatch(CommandTask task) {
        return deviceDispatchExecutor.submit(() -> {
            var request = task.getRequest();
            log.info("Sending command {} to device {}", task.getCommandId(), request.deviceId());
            simulateDeviceCall();
            var completedAt = Instant.now();
            return new CommandResponse(
                    task.getCommandId(),
                    request.deviceId(),
                    CommandStatus.SENT,
                    "Command delivered",
                    task.getEnqueuedAt(),
                    completedAt);
        });
    }

    private void simulateDeviceCall() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(Math.min(properties.getExecutor().getCommandTimeout().toMillis(), 250));
    }
}
