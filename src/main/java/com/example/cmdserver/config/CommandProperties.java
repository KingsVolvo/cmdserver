package com.example.cmdserver.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "command")
public class CommandProperties {

    private final QueueProperties queue = new QueueProperties();
    private final ExecutorProperties executor = new ExecutorProperties();

    public QueueProperties getQueue() {
        return queue;
    }

    public ExecutorProperties getExecutor() {
        return executor;
    }

    public static class QueueProperties {
        private int capacity = 1000;
        private int consumerThreads = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        private Duration offerTimeout = Duration.ofSeconds(2);

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getConsumerThreads() {
            return consumerThreads;
        }

        public void setConsumerThreads(int consumerThreads) {
            this.consumerThreads = consumerThreads;
        }

        public Duration getOfferTimeout() {
            return offerTimeout;
        }

        public void setOfferTimeout(Duration offerTimeout) {
            this.offerTimeout = offerTimeout;
        }
    }

    public static class ExecutorProperties {
        private int threads = Math.max(4, Runtime.getRuntime().availableProcessors());
        private Duration commandTimeout = Duration.ofSeconds(10);

        public int getThreads() {
            return threads;
        }

        public void setThreads(int threads) {
            this.threads = threads;
        }

        public Duration getCommandTimeout() {
            return commandTimeout;
        }

        public void setCommandTimeout(Duration commandTimeout) {
            this.commandTimeout = commandTimeout;
        }
    }
}
