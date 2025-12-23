package com.example.cmdserver.queue;

public class CommandQueueFullException extends RuntimeException {

    public CommandQueueFullException(String message) {
        super(message);
    }
}
