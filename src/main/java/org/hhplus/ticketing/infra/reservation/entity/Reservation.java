package org.hhplus.ticketing.infra.reservation.entity;

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
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;                     // 예약ID (키값)

    @Column(name = "concert_seat_id", nullable = false)
    private Long concertSeatId;                     // 콘서트좌석ID

    @Column(name = "user_id", nullable = false)
    private Long userId;                            // 유저ID

    @Column(name = "reservation_at", nullable = false)
    private LocalDateTime reservationAt;            // 예약시간

    @Column(name = "status", nullable = false)
    private String status;                          // 예약상태 (예약됨[Reserved]/점유중[Occupied]/만료됨[Expired]/최소됨[Cancelled])

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;                // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;                // 수정일자
}
