package org.hhplus.ticketing.domain.concert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.concert.model.enums.ReservationStatus;
import org.hhplus.ticketing.domain.queue.model.QueueDomain;
import org.hhplus.ticketing.domain.queue.model.enums.TokenStatus;

import java.time.LocalDateTime;

/**
 * 도메인 객체: Reservation
 * 예약 정보 관리.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDomain {
    private Long reservationId;          // 예약ID (키값)
    private Long concertSeatId;          // 콘서트좌석ID
    private Long userId;                 // 유저ID
    private LocalDateTime reservationAt; // 예약시간
    private ReservationStatus status;    // 예약상태 (예약됨[RESERVED]/점유[OCCUPIED]/만료[EXPIRED])

    public static ReservationDomain createReservation(Long concertSeatId, Long userId) {
        return ReservationDomain.builder()
                .concertSeatId(concertSeatId)
                .userId(userId)
                .reservationAt(LocalDateTime.now())
                .status(ReservationStatus.RESERVED)
                .build();
    }

    public ReservationDomain updateReservationExpired() {
        return ReservationDomain.builder()
                .reservationId(this.reservationId)
                .concertSeatId(this.concertSeatId)
                .userId(this.userId)
                .reservationAt(this.reservationAt)
                .status(ReservationStatus.EXPIRED)
                .build();
    }

    public ReservationDomain updateReservationOccupied() {
        return ReservationDomain.builder()
                .reservationId(this.reservationId)
                .concertSeatId(this.concertSeatId)
                .userId(this.userId)
                .reservationAt(this.reservationAt)
                .status(ReservationStatus.OCCUPIED)
                .build();
    }

}