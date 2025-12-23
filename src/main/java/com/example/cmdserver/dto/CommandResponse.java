package com.example.cmdserver.dto;

import com.example.cmdserver.model.CommandStatus;
import java.time.Instant;

public record CommandResponse(
        String commandId,
        String deviceId,
        CommandStatus status,
        String message,
        Instant acceptedAt,
        Instant completedAt
) { }
