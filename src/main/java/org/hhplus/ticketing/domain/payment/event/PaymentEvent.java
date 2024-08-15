package org.hhplus.ticketing.domain.payment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.outbox.model.OutboxCommand;
import org.hhplus.ticketing.domain.payment.model.constants.PaymentConstants;
import org.hhplus.ticketing.support.util.JsonUtil;

public class PaymentEvent {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Success {
        private String token;
        private Long userId;
        private Long reservationId;
        private int price;

        public OutboxCommand.save toOutboxSaveCommand() {
            return OutboxCommand.save
                    .builder()
                    .messageKey(reservationId.toString())
                    .domainType(PaymentConstants.DOMAIN)
                    .eventType(PaymentConstants.SUCCESS_EVENT)
                    .message(JsonUtil.toJson(this))
                    .build();
        }

        public OutboxCommand.updateSent toOutboxUpdateCommand() {
            return OutboxCommand.updateSent
                    .builder()
                    .messageKey(reservationId.toString())
                    .domainType(PaymentConstants.DOMAIN)
                    .eventType(PaymentConstants.SUCCESS_EVENT)
                    .build();
        }
    }
}