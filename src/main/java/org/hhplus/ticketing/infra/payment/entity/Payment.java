package org.hhplus.ticketing.infra.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.payment.model.PaymentDomain;
import org.hhplus.ticketing.domain.payment.model.enums.PaymentStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;                 // 결제ID (키값)

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;             // 예약ID

    @Column(name = "price", nullable = false)
    private Integer price;                  // 결제가격

    @Column(name = "payment_at", nullable = false)
    private LocalDateTime paymentAt;        // 결제시간

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;           // 결제상태 (결제완료[COMPLETED]/결제취소[CANCELED])

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;        // 생성일자

    @Column(nullable = false)
    private LocalDateTime updatedAt;        // 수정일자

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Payment from(PaymentDomain domain) {
        return Payment.builder()
                .paymentId(domain.getPaymentId())
                .reservationId(domain.getReservationId())
                .price(domain.getPrice())
                .paymentAt(domain.getPaymentAt())
                .status(domain.getStatus())
                .build();
    }

    public PaymentDomain toDomain() {
        return PaymentDomain.builder()
                .paymentId(this.getPaymentId())
                .reservationId(this.getReservationId())
                .price(this.getPrice())
                .paymentAt(this.getPaymentAt())
                .status(this.getStatus())
                .build();
    }

}
