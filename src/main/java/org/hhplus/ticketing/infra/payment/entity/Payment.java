package org.hhplus.ticketing.infra.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "status", nullable = false)
    private String status;                  // 결제상태 (결제완료/결제취소)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;        // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;        // 수정일자

}
