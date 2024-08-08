package org.hhplus.ticketing.domain.payment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.client.DataPlatformClient;
import org.hhplus.ticketing.domain.common.client.PushClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PushClient pushClient;
    private final DataPlatformClient dataPlatformClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentEvent.Success event) {
        log.info("결제 리슨!");
        try {
            log.info("Starting push notification...");
            pushClient.sendKakaotalk("결제완료", event);
            log.info("Push notification completed.");

            log.info("Starting data platform sync...");
            dataPlatformClient.send("결제완료", event);
            log.info("Data platform sync completed.");
        } catch (Exception e) {
            log.error("Error in event listener", e);
        }
    }
}
