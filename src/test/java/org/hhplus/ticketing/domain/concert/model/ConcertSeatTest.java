package org.hhplus.ticketing.domain.concert.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
class ConcertSeatTest {
    @Test
    @DisplayName("🟢 콘서트좌석_객체_생성_테스트_콘서트좌석정보로_객체가_생성된다")
    void createTest_콘서트좌석_객체_생성_테스트_콘서트좌석정보로_객체가_생성된다() {
        // Given
        Long concertOptionId = 1L;
        int seatNumber = 1;
        ConcertSeat.Grade grade = ConcertSeat.Grade.VIP;

        // When
        ConcertSeat seat = ConcertSeat.create(concertOptionId, seatNumber, grade);

        // Then
        assertThat(seat.getConcertOptionId()).isEqualTo(concertOptionId);
        assertThat(seat.getSeatNumber()).isEqualTo(seatNumber);
        assertThat(seat.getGrade()).isEqualTo(grade);
        assertThat(seat.getPrice()).isEqualTo(grade.getPrice());
        assertThat(seat.getStatus()).isEqualTo(ConcertSeat.Status.AVAILABLE);
    }
    @Test
    @DisplayName("🟢 좌석_사용가능_상태변경_테스트_좌석상태가_사용가능으로_변경된다")
    void setAvailableTest_좌석_사용가능_상태변경_테스트_좌석상태가_사용가능으로_변경된다() {
        // Given
        Long concertSeatId = 1L;
        Long concertOptionId = 1L;
        int seatNumber = 1;
        ConcertSeat.Grade grade = ConcertSeat.Grade.VIP;
        ConcertSeat.Status initialStatus = ConcertSeat.Status.RESERVED;

        ConcertSeat concertSeat = ConcertSeat.builder()
                .concertSeatId(concertSeatId)
                .concertOptionId(concertOptionId)
                .seatNumber(seatNumber)
                .grade(grade)
                .price(grade.getPrice())
                .status(initialStatus)
                .build();

        // When
        concertSeat.setAvailable();

        // Then
        assertThat(concertSeat.getStatus()).isEqualTo(ConcertSeat.Status.AVAILABLE);
    }

    @Test
    @DisplayName("🟢 좌석_예약됨_상태변경_테스트_좌석상태가_예약됨으로_변경된다")
    void setReservedTest_좌석_예약됨_상태변경_테스트_좌석상태가_예약됨으로_변경된다() {
        // Given
        Long concertSeatId = 1L;
        Long concertOptionId = 1L;
        int seatNumber = 1;
        ConcertSeat.Grade grade = ConcertSeat.Grade.VIP;
        ConcertSeat.Status initialStatus = ConcertSeat.Status.AVAILABLE;

        ConcertSeat concertSeat = ConcertSeat.builder()
                .concertSeatId(concertSeatId)
                .concertOptionId(concertOptionId)
                .seatNumber(seatNumber)
                .grade(grade)
                .price(grade.getPrice())
                .status(initialStatus)
                .build();

        // When
        concertSeat.setReserved();

        // Then
        assertThat(concertSeat.getStatus()).isEqualTo(ConcertSeat.Status.RESERVED);
    }

    @Test
    @DisplayName("🟢 좌석_점유_상태변경_테스트_좌석상태가_점유로_변경된다")
    void setOccupiedTest_좌석_점유_상태변경_테스트_좌석상태가_점유로_변경된다() {
        // Given
        Long concertSeatId = 1L;
        Long concertOptionId = 1L;
        int seatNumber = 1;
        ConcertSeat.Grade grade = ConcertSeat.Grade.VIP;
        ConcertSeat.Status initialStatus = ConcertSeat.Status.RESERVED;

        ConcertSeat concertSeat = ConcertSeat.builder()
                .concertSeatId(concertSeatId)
                .concertOptionId(concertOptionId)
                .seatNumber(seatNumber)
                .grade(grade)
                .price(grade.getPrice())
                .status(initialStatus)
                .build();
        // When
        concertSeat.setOccupied();

        // Then
        assertThat(concertSeat.getStatus()).isEqualTo(ConcertSeat.Status.OCCUPIED);
    }
}