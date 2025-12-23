package com.example.cmdserver.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.cmdserver.dto.CommandRequest;
import com.example.cmdserver.model.CommandStatus;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommandQueueServiceTest {

    @Autowired
    private CommandQueueService queueService;

    @Test
    void dispatchesCommandThroughQueue() throws Exception {
        var request = new CommandRequest("device-1", "reboot", Map.of("delaySeconds", 5));

        var response = queueService.enqueue(request).get(2, TimeUnit.SECONDS);

        assertThat(response.commandId()).isNotBlank();
        assertThat(response.status()).isEqualTo(CommandStatus.SENT);
        assertThat(response.completedAt()).isNotNull();
    }
}
