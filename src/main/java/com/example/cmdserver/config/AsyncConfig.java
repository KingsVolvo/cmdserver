package com.example.cmdserver.config;

import com.example.cmdserver.queue.CommandTask;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableConfigurationProperties(CommandProperties.class)
public class AsyncConfig {

    @Bean
    public BlockingQueue<CommandTask> commandQueue(CommandProperties properties) {
        return new LinkedBlockingQueue<>(properties.getQueue().getCapacity());
    }

    @Bean(destroyMethod = "shutdown")
    public ListeningExecutorService commandConsumerExecutor(CommandProperties properties) {
        return MoreExecutors.listeningDecorator(
                Executors.newFixedThreadPool(
                        properties.getQueue().getConsumerThreads(),
                        new ThreadFactoryBuilder().setNameFormat("cmd-consumer-%d").build()));
    }

    @Bean(destroyMethod = "shutdown")
    public ListeningExecutorService deviceDispatchExecutor(CommandProperties properties) {
        return MoreExecutors.listeningDecorator(
                Executors.newFixedThreadPool(
                        properties.getExecutor().getThreads(),
                        new ThreadFactoryBuilder().setNameFormat("cmd-dispatch-%d").build()));
    }
}
