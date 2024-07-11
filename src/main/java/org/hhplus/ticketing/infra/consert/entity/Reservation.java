package org.hhplus.ticketing.infra.consert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.consert.model.ReservationDomain;
import org.hhplus.ticketing.domain.consert.model.enums.ReservationStatus;
import org.hhplus.ticketing.domain.user.model.UserPointDomain;
import org.hhplus.ticketing.infra.queue.entity.Queue;
import org.hhplus.ticketing.infra.user.entity.UserPoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;                // 예약상태 (예약됨[RESERVED]/점유[OCCUPIED]/만료[EXPIRED])
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;                // 생성일자

    @Column(nullable = false)
    private LocalDateTime updatedAt;                // 수정일자

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Reservation from(ReservationDomain domain) {
        return Reservation.builder()
                .reservationId(domain.getReservationId())
                .concertSeatId(domain.getConcertSeatId())
                .userId(domain.getUserId())
                .reservationAt(domain.getReservationAt())
                .build();
    }

    public ReservationDomain toDomain() {
        return ReservationDomain.builder()
                .reservationId(this.getReservationId())
                .concertSeatId(this.getConcertSeatId())
                .userId(this.getUserId())
                .reservationAt(this.getReservationAt())
                .build();
    }
}
