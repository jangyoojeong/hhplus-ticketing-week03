package org.hhplus.ticketing.interfaces.controller.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.application.payment.PaymentResult;

public class PaymentResponse {

    // 결제 response
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pay {
        private Long paymentId;                 // 결제ID

        public static Pay from(PaymentResult.Pay result) {
            return Pay.builder()
                    .paymentId(result.getPaymentId())
                    .build();
        }
    }
}
