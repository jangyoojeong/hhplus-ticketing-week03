package org.hhplus.ticketing.infra.common.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.messaging.MessageProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageProducer implements MessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public CompletableFuture<SendResult<String, String>> send(String topic, String key, String message) {
        return kafkaTemplate.send(topic, key, message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message [{}] sent successfully to topic [{}] with offset [{}]",
                                message, topic, result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send message [{}] to topic [{}] due to error: {}",
                                message, topic, ex.getMessage(), ex);
                    }
                });
    }
}
