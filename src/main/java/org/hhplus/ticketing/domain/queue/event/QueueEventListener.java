package org.hhplus.ticketing.domain.queue.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.payment.event.PaymentEvent;
import org.hhplus.ticketing.domain.queue.QueueService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueEventListener {

    private final QueueService queueService;

    @Order(3)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void paymentSuccessHandler(PaymentEvent.Success event) {
        queueService.expireToken(event.getToken());
    }
}
