package com.example.cmdserver.controller;

import com.example.cmdserver.dto.CommandRequest;
import com.example.cmdserver.dto.CommandResponse;
import com.example.cmdserver.service.CommandQueueService;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import jakarta.validation.Valid;


import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/commands")
@Validated
public class CommandController {

    private static final Logger log = LoggerFactory.getLogger(CommandQueueService.class);

    private final CommandQueueService queueService;

    public CommandController(CommandQueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping
    public DeferredResult<ResponseEntity<CommandResponse>> submit(@Valid @RequestBody CommandRequest request) {
        var deferredResult = new DeferredResult<ResponseEntity<CommandResponse>>();
        var responseFuture = queueService.enqueue(request);

        Futures.addCallback(responseFuture, new FutureCallback<>() {
            @Override
            public void onSuccess(CommandResponse result) {
                log.debug("Future command {} processed for device {}", result.commandId(), result.deviceId());
                deferredResult.setResult(ResponseEntity.ok(result));
            }

            @Override
            public void onFailure(Throwable t) {
                deferredResult.setErrorResult(t);
            }
        }, MoreExecutors.directExecutor());

        return deferredResult;
    }
}
