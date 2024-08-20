package org.hhplus.ticketing.domain.common.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSender {

    private final MessageProducer messageProducer;

    public CompletableFuture<Boolean> sendMessage(String eventType, String messageKey, String message) {
        return messageProducer.send(eventType, messageKey, message)
                .thenApply(result -> true)
                .exceptionally(ex -> {
                    log.error("Failed to send message [{}] due to error: {}",message, ex.getMessage(), ex);
                    return false;
                });
    }
}

