package com.example.cmdserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record CommandRequest(
        @NotBlank(message = "deviceId is required")
        @Size(max = 128, message = "deviceId is too long")
        String deviceId,

        @NotBlank(message = "commandType is required")
        @Size(max = 128, message = "commandType is too long")
        String commandType,

        Map<String, Object> payload
) { }
