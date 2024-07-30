package org.hhplus.ticketing.interfaces.controller.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.payment.model.PaymentResult;

// 결제 컨트롤러 단위테스트입니다.
public class PaymentResponse {

    // 결제 진행 response
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentProcessingResponse {

        private Long paymentId;                 // 결제ID
        private Long userId;                    // 사용자ID
        private int point;                      // 포인트 (결제 후 포인트)

        public static PaymentResponse.PaymentProcessingResponse from(PaymentResult.RequestPaymentResult result) {
            return PaymentProcessingResponse.builder()
                    .paymentId(result.getPaymentId())
                    .userId(result.getUserId())
                    .point(result.getPoint())
                    .build();
        }

    }
}
