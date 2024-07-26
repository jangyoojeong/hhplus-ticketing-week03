package org.hhplus.ticketing.infra.concert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.concert.model.Reservation;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reservation")
public class ReservationEntity {

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

    @Column(name = "price", nullable = false)
    private int price;                              // 좌석가격

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Reservation.Status status;              // 예약상태 (예약됨[RESERVED]/점유[OCCUPIED]/만료[EXPIRED])
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;                // 생성일자

    @Column(nullable = false)
    private LocalDateTime updatedAt;                // 수정일자

    @Version
    @Column(name = "version")
    private Long version;                           // 낙관적 락 버전 필드

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static ReservationEntity from(Reservation domain) {
        return ReservationEntity.builder()
                .reservationId(domain.getReservationId())
                .concertSeatId(domain.getConcertSeatId())
                .userId(domain.getUserId())
                .reservationAt(domain.getReservationAt())
                .price(domain.getPrice())
                .status(domain.getStatus())
                .version(domain.getVersion())
                .build();
    }

    public Reservation toDomain() {
        return Reservation.builder()
                .reservationId(this.getReservationId())
                .concertSeatId(this.getConcertSeatId())
                .userId(this.getUserId())
                .reservationAt(this.getReservationAt())
                .price(this.getPrice())
                .status(this.getStatus())
                .version(this.getVersion())
                .build();
    }
}
