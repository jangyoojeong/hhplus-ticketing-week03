package org.hhplus.ticketing.infra.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.payment.model.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment")
public class PaymentEntity {

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
    private Payment.Status status;          // 결제상태 (결제완료[COMPLETED]/결제취소[CANCELED])

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;         // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;         // 수정일자

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static PaymentEntity from(Payment domain) {
        return PaymentEntity.builder()
                .paymentId(domain.getPaymentId())
                .reservationId(domain.getReservationId())
                .price(domain.getPrice())
                .paymentAt(domain.getPaymentAt())
                .status(domain.getStatus())
                .build();
    }

    public Payment toDomain() {
        return Payment.builder()
                .paymentId(this.getPaymentId())
                .reservationId(this.getReservationId())
                .price(this.getPrice())
                .paymentAt(this.getPaymentAt())
                .status(this.getStatus())
                .build();
    }

    public static List<Payment> toDomainList(List<PaymentEntity> entityList) {
        return entityList.stream()
                .map(PaymentEntity::toDomain)
                .collect(Collectors.toList());
    }

}
