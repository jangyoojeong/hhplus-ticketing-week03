package org.hhplus.ticketing.application.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PaymentCreteria {

    // 결제 creteria
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pay {
        private Long userId;                    // 사용자ID
        private Long reservationId;             // 예약ID
        private int price;                      // 결제금액
        private String token;                   // 토큰
    }
}
