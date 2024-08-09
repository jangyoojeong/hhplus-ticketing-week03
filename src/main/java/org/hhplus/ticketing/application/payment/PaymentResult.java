package org.hhplus.ticketing.application.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PaymentResult {

    // 결제 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pay {
        private Long paymentId;                 // 결제ID
    }

}
