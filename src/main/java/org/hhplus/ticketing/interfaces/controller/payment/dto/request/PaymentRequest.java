package org.hhplus.ticketing.interfaces.controller.payment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;

public class PaymentRequest {

    // 결제 진행 request
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentProcessingRequest {

        @NotNull(message = "사용자 ID는 비어 있을 수 없습니다.")
        private Long userId;                    // 사용자ID

        @NotNull(message = "예약 ID는 비어 있을 수 없습니다.")
        private Long reservationId;             // 예약ID

        @Min(value = 1, message = "결제 금액은 최소 1 이상이어야 합니다.")
        private int price;                      // 결제금액

        public PaymentCommand.PaymentProcessingCommand toCommand() {
            return PaymentCommand.PaymentProcessingCommand.builder()
                    .userId(this.getUserId())
                    .reservationId(this.getReservationId())
                    .price(this.getPrice())
                    .build();
        }
    }

}
