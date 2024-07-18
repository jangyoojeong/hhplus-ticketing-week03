package org.hhplus.ticketing.domain.concert.model;

import org.hhplus.ticketing.domain.concert.model.enums.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReservationDomainTest {

    @Test
    @DisplayName("[성공테스트] 예약_객체_생성_테스트_예약됨_상태의_객체가_생성된다")
    void createReservationTest_예약_객체_생성_테스트_예약됨_상태의_객체가_생성된다() {
        Long concertSeatId = 1L;
        Long userId = 1L;

        ReservationDomain reservationDomain = ReservationDomain.createReservation(concertSeatId, userId);

        assertThat(reservationDomain.getConcertSeatId()).isEqualTo(concertSeatId);
        assertThat(reservationDomain.getUserId()).isEqualTo(userId);
        assertThat(reservationDomain.getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }

    @Test
    @DisplayName("[성공테스트] 예약_만료_상태변경_테스트_만료_상태로_변경된다")
    void updateReservationExpiredTest_예약_만료_상태변경_테스트_만료_상태로_변경된다() {
        Long reservationId = 1L;
        Long concertSeatId = 1L;
        Long userId = 1L;
        LocalDateTime reservationAt = LocalDateTime.now();

        ReservationDomain reservationDomain = ReservationDomain.builder()
                .reservationId(reservationId)
                .concertSeatId(concertSeatId)
                .userId(userId)
                .reservationAt(reservationAt)
                .status(ReservationStatus.RESERVED)
                .build();

        ReservationDomain updatedReservationDomain = reservationDomain.updateReservationExpired();

        assertThat(updatedReservationDomain.getReservationId()).isEqualTo(reservationId);
        assertThat(updatedReservationDomain.getConcertSeatId()).isEqualTo(concertSeatId);
        assertThat(updatedReservationDomain.getUserId()).isEqualTo(userId);
        assertThat(updatedReservationDomain.getReservationAt()).isEqualTo(reservationAt);
        assertThat(updatedReservationDomain.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
    }

    @Test
    @DisplayName("[성공테스트] 예약_점유_상태변경_테스트_점유_상태로_변경된다")
    void updateReservationOccupiedTest_예약_점유_상태변경_테스트_점유_상태로_변경된다() {
        Long reservationId = 1L;
        Long concertSeatId = 1L;
        Long userId = 1L;
        LocalDateTime reservationAt = LocalDateTime.now();

        ReservationDomain reservationDomain = ReservationDomain.builder()
                .reservationId(reservationId)
                .concertSeatId(concertSeatId)
                .userId(userId)
                .reservationAt(reservationAt)
                .status(ReservationStatus.RESERVED)
                .build();

        ReservationDomain updatedReservationDomain = reservationDomain.updateReservationOccupied();

        assertThat(updatedReservationDomain.getReservationId()).isEqualTo(reservationId);
        assertThat(updatedReservationDomain.getConcertSeatId()).isEqualTo(concertSeatId);
        assertThat(updatedReservationDomain.getUserId()).isEqualTo(userId);
        assertThat(updatedReservationDomain.getReservationAt()).isEqualTo(reservationAt);
        assertThat(updatedReservationDomain.getStatus()).isEqualTo(ReservationStatus.OCCUPIED);
    }
}