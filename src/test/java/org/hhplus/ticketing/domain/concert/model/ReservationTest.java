package org.hhplus.ticketing.domain.concert.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReservationTest {

    @Test
    @DisplayName("🟢 [예약_객체_생성_테스트]")
    void createTest_예약됨_상태의_객체가_생성된다() {
        Long concertSeatId = 1L;
        Long userId = 1L;
        int price = 50000;

        Reservation reservation = Reservation.create(concertSeatId, userId, price);

        assertThat(reservation.getConcertSeatId()).isEqualTo(concertSeatId);
        assertThat(reservation.getUserId()).isEqualTo(userId);
        assertThat(reservation.getPrice()).isEqualTo(price);
        assertThat(reservation.getStatus()).isEqualTo(Reservation.Status.RESERVED);
    }

    @Test
    @DisplayName("🟢 [예약_만료_상태변경_테스트]")
    void setExpiredTest_만료_상태로_변경된다() {
        Long reservationId = 1L;
        Long concertSeatId = 1L;
        Long userId = 1L;
        LocalDateTime reservationAt = LocalDateTime.now().minusMinutes(7);

        Reservation reservation = Reservation.builder()
                .reservationId(reservationId)
                .concertSeatId(concertSeatId)
                .userId(userId)
                .reservationAt(reservationAt)
                .status(Reservation.Status.RESERVED)
                .build();

        Reservation updatedreservation = reservation.setExpired();

        assertThat(updatedreservation.getReservationId()).isEqualTo(reservationId);
        assertThat(updatedreservation.getConcertSeatId()).isEqualTo(concertSeatId);
        assertThat(updatedreservation.getUserId()).isEqualTo(userId);
        assertThat(updatedreservation.getReservationAt()).isEqualTo(reservationAt);
        assertThat(updatedreservation.getStatus()).isEqualTo(Reservation.Status.EXPIRED);
    }

    @Test
    @DisplayName("🟢 [예약_점유_상태변경_테스트]")
    void setOccupiedTest_점유_상태로_변경된다() {
        Long reservationId = 1L;
        Long concertSeatId = 1L;
        Long userId = 1L;
        LocalDateTime reservationAt = LocalDateTime.now();

        Reservation reservation = Reservation.builder()
                .reservationId(reservationId)
                .concertSeatId(concertSeatId)
                .userId(userId)
                .reservationAt(reservationAt)
                .status(Reservation.Status.RESERVED)
                .build();

        Reservation updatedreservation = reservation.setOccupied();

        assertThat(updatedreservation.getReservationId()).isEqualTo(reservationId);
        assertThat(updatedreservation.getConcertSeatId()).isEqualTo(concertSeatId);
        assertThat(updatedreservation.getUserId()).isEqualTo(userId);
        assertThat(updatedreservation.getReservationAt()).isEqualTo(reservationAt);
        assertThat(updatedreservation.getStatus()).isEqualTo(Reservation.Status.OCCUPIED);
    }
}