package org.hhplus.ticketing.interfaces.controller.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.application.payment.PaymentCreteria;

public class PaymentRequest {

    // 결제 request
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pay {

        @NotNull(message = "사용자 ID는 비어 있을 수 없습니다.")
        private Long userId;                    // 사용자ID

        @NotNull(message = "예약 ID는 비어 있을 수 없습니다.")
        private Long reservationId;             // 예약ID

        public PaymentCreteria.Pay toCreteria(String token) {
            return PaymentCreteria.Pay.builder()
                    .userId(this.getUserId())
                    .reservationId(this.getReservationId())
                    .token(token)
                    .build();
        }
    }

}
