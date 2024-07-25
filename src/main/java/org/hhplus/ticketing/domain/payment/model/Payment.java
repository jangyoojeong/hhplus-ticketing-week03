package org.hhplus.ticketing.domain.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 도메인 객체: Payment
 * 결제 정보 관리.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    private Long paymentId;                 // 결제ID (키값)
    private Long reservationId;             // 예약ID
    private int price;                      // 결제가격
    private LocalDateTime paymentAt;        // 결제시간
    private Status status;                  // 결제상태 (결제완료[COMPLETED]/결제취소[CANCELED])

    public static Payment from(PaymentCommand.PaymentProcessingCommand command) {
        return Payment.builder()
                .reservationId(command.getReservationId())
                .price(command.getPrice())
                .paymentAt(LocalDateTime.now())
                .status(Status.COMPLETED)
                .build();
    }

    public enum Status {
        COMPLETED,       // 결제완료
        CANCELED         // 결제취소
    }
}
