package org.hhplus.ticketing.domain.common.messaging;

import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface MessageProducer {
    CompletableFuture<SendResult<String, String>> send(String topic, String messageKey, String message);
}
