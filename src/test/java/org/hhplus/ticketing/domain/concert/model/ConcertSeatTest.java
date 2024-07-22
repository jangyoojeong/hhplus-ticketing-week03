package org.hhplus.ticketing.domain.concert.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
class ConcertSeatTest {

    @Test
    @DisplayName("🟢 좌석_사용가능_상태변경_테스트_좌석상태가_사용가능으로_변경된다")
    void setAvailableTest_좌석_사용가능_상태변경_테스트_좌석상태가_사용가능으로_변경된다() {
        Long concertSeatId = 1L;
        Long concertOptionId = 1L;
        int seatNumber = 1;
        ConcertSeat.Grade grade = ConcertSeat.Grade.VIP;
        ConcertSeat.Status initialStatus = ConcertSeat.Status.RESERVED;

        ConcertSeat concertSeat = ConcertSeat.create(concertSeatId, concertOptionId, seatNumber, grade, initialStatus);
        concertSeat.setAvailable();

        assertThat(concertSeat.getStatus()).isEqualTo(ConcertSeat.Status.AVAILABLE);
    }

    @Test
    @DisplayName("🟢 좌석_예약됨_상태변경_테스트_좌석상태가_예약됨으로_변경된다")
    void setReservedTest_좌석_예약됨_상태변경_테스트_좌석상태가_예약됨으로_변경된다() {
        Long concertSeatId = 1L;
        Long concertOptionId = 1L;
        int seatNumber = 1;
        ConcertSeat.Grade grade = ConcertSeat.Grade.VIP;
        ConcertSeat.Status initialStatus = ConcertSeat.Status.AVAILABLE;

        ConcertSeat concertSeat = ConcertSeat.create(concertSeatId, concertOptionId, seatNumber, grade, initialStatus);
        concertSeat.setReserved();

        assertThat(concertSeat.getStatus()).isEqualTo(ConcertSeat.Status.RESERVED);
    }

    @Test
    @DisplayName("🟢 좌석_점유_상태변경_테스트_좌석상태가_점유로_변경된다")
    void setOccupiedTest_좌석_점유_상태변경_테스트_좌석상태가_점유로_변경된다() {
        Long concertSeatId = 1L;
        Long concertOptionId = 1L;
        int seatNumber = 1;
        ConcertSeat.Grade grade = ConcertSeat.Grade.VIP;
        ConcertSeat.Status initialStatus = ConcertSeat.Status.RESERVED;

        ConcertSeat concertSeat = ConcertSeat.create(concertSeatId, concertOptionId, seatNumber, grade, initialStatus);
        concertSeat.setOccupied();

        assertThat(concertSeat.getStatus()).isEqualTo(ConcertSeat.Status.OCCUPIED);
    }

    @Test
    @DisplayName("🟢 좌석_객체_생성_테스트_좌석객체가_정상적으로_생성된다")
    void createConcertSeatTest_좌석_객체_생성_테스트_좌석객체가_정상적으로_생성된다() {
        Long concertSeatId = 1L;
        Long concertOptionId = 1L;
        int seatNumber = 1;
        ConcertSeat.Grade grade = ConcertSeat.Grade.VIP;
        ConcertSeat.Status status = ConcertSeat.Status.AVAILABLE;

        ConcertSeat concertSeat = ConcertSeat.create(concertSeatId, concertOptionId, seatNumber, grade, status);

        assertThat(concertSeat.getConcertSeatId()).isEqualTo(concertSeatId);
        assertThat(concertSeat.getConcertOptionId()).isEqualTo(concertOptionId);
        assertThat(concertSeat.getSeatNumber()).isEqualTo(seatNumber);
        assertThat(concertSeat.getGrade()).isEqualTo(grade);
        assertThat(concertSeat.getPrice()).isEqualTo(grade.getPrice());
        assertThat(concertSeat.getStatus()).isEqualTo(status);
    }
}