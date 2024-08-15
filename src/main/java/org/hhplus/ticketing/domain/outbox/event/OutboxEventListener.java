package org.hhplus.ticketing.domain.outbox.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.outbox.OutboxService;
import org.hhplus.ticketing.domain.common.messaging.MessageSender;
import org.hhplus.ticketing.domain.payment.event.PaymentEvent;
import org.hhplus.ticketing.domain.payment.model.constants.PaymentConstants;
import org.hhplus.ticketing.support.util.JsonUtil;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventListener {

    private final OutboxService outboxService;
    private final MessageSender messageSender;

    @Order(4)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(PaymentEvent.Success event) {
        outboxService.save(event.toOutboxSaveCommand());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendSuccess(PaymentEvent.Success event) {
        messageSender.sendMessage(PaymentConstants.SUCCESS_EVENT, event.getReservationId().toString(), JsonUtil.toJson(event))
                .thenAccept(success -> {
                    if (!success) return;
                    outboxService.updateSent(event.toOutboxUpdateCommand());
                });
    }
}
