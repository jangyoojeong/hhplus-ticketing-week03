package org.hhplus.ticketing.domain.payment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PaymentEvent {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class Success {
        private final String token;
        private final Long userId;
        private final Long reservationId;
        private int price;                      // 결제금액
    }
}