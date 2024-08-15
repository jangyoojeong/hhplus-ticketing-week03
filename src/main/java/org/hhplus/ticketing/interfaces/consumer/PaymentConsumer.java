package org.hhplus.ticketing.interfaces.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.client.DataPlatformClient;
import org.hhplus.ticketing.domain.common.client.PushClient;
import org.hhplus.ticketing.domain.payment.event.PaymentEvent;
import org.hhplus.ticketing.support.util.JsonUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PushClient pushClient;
    private final DataPlatformClient dataPlatformClient;

    @KafkaListener(topics = "${spring.kafka.topic.payment-success}", groupId = "${spring.kafka.consumer.group-ids.success}")
    public void handlePaymentSuccess(String message) {
        log.info("Received Kafka message: {}", message);

        PaymentEvent.Success event = JsonUtil.fromJson(message, PaymentEvent.Success.class);
        pushClient.sendKakaotalk("결제완료", event);
        dataPlatformClient.send("결제완료", event);

        log.info("Successfully processed payment success event: {}", event);
    }
}
