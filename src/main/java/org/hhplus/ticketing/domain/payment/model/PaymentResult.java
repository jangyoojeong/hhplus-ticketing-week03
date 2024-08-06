package org.hhplus.ticketing.domain.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PaymentResult {

    // 결제 진행 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Pay {

        private Long paymentId;                 // 결제ID
        private Long userId;                    // 사용자ID
        private int point;                      // 포인트 (결제 후 포인트)

        public static Pay from(Payment domain) {
            return Pay.builder()
                    .paymentId(domain.getPaymentId())
                    .build();
        }
    }

}
