package com.example.cmdserver.queue;

import com.example.cmdserver.dto.CommandRequest;
import com.example.cmdserver.dto.CommandResponse;
import com.google.common.util.concurrent.SettableFuture;
import java.time.Instant;

public class CommandTask {

    private final String commandId;
    private final CommandRequest request;
    private final Instant enqueuedAt;
    private final SettableFuture<CommandResponse> resultFuture;

    public CommandTask(String commandId,
                       CommandRequest request,
                       Instant enqueuedAt,
                       SettableFuture<CommandResponse> resultFuture) {
        this.commandId = commandId;
        this.request = request;
        this.enqueuedAt = enqueuedAt;
        this.resultFuture = resultFuture;
    }

    public String getCommandId() {
        return commandId;
    }

    public CommandRequest getRequest() {
        return request;
    }

    public Instant getEnqueuedAt() {
        return enqueuedAt;
    }

    public SettableFuture<CommandResponse> getResultFuture() {
        return resultFuture;
    }
}
