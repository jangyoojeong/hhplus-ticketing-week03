package org.hhplus.ticketing.domain.concert.model;

import org.hhplus.ticketing.domain.concert.model.enums.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
class ConcertSeatDomainTest {

    @Test
    @DisplayName("[성공테스트] 좌석_사용가능_상태변경_테스트_좌석상태가_사용가능으로_변경된다")
    void updateSeatAvailableTest_좌석_사용가능_상태변경_테스트_좌석상태가_사용가능으로_변경된다() {
        Long concertSeatId = 1L;
        Long concertOptionId = 1L;
        int seatNumber = 1;
        SeatStatus initialStatus = SeatStatus.RESERVED;

        ConcertSeatDomain concertSeat = new ConcertSeatDomain(concertSeatId, concertOptionId, seatNumber, initialStatus);
        concertSeat.updateSeatAvailable();

        assertThat(concertSeat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("[성공테스트] 좌석_예약됨_상태변경_테스트_좌석상태가_예약됨으로_변경된다")
    void updateSeatReservedTest_좌석_예약됨_상태변경_테스트_좌석상태가_예약됨으로_변경된다() {
        Long concertSeatId = 1L;
        Long concertOptionId = 1L;
        int seatNumber = 1;
        SeatStatus initialStatus = SeatStatus.AVAILABLE;

        ConcertSeatDomain concertSeat = new ConcertSeatDomain(concertSeatId, concertOptionId, seatNumber, initialStatus);
        concertSeat.updateSeatReserved();

        assertThat(concertSeat.getStatus()).isEqualTo(SeatStatus.RESERVED);
    }

    @Test
    @DisplayName("[성공테스트] 좌석_점유_상태변경_테스트_좌석상태가_점유로_변경된다")
    void updateSeatOccupiedTest_좌석_점유_상태변경_테스트_좌석상태가_점유로_변경된다() {
        Long concertSeatId = 1L;
        Long concertOptionId = 1L;
        int seatNumber = 1;
        SeatStatus initialStatus = SeatStatus.RESERVED;

        ConcertSeatDomain concertSeat = new ConcertSeatDomain(concertSeatId, concertOptionId, seatNumber, initialStatus);
        concertSeat.updateSeatOccupied();

        assertThat(concertSeat.getStatus()).isEqualTo(SeatStatus.OCCUPIED);
    }

    @Test
    @DisplayName("[성공테스트] 좌석_객체_생성_테스트_좌석객체가_정상적으로_생성된다")
    void createConcertSeatTest_좌석_객체_생성_테스트_좌석객체가_정상적으로_생성된다() {
        Long concertSeatId = 1L;
        Long concertOptionId = 1L;
        int seatNumber = 1;
        SeatStatus status = SeatStatus.AVAILABLE;

        ConcertSeatDomain concertSeat = new ConcertSeatDomain(concertSeatId, concertOptionId, seatNumber, status);

        assertThat(concertSeat.getConcertSeatId()).isEqualTo(concertSeatId);
        assertThat(concertSeat.getConcertOptionId()).isEqualTo(concertOptionId);
        assertThat(concertSeat.getSeatNumber()).isEqualTo(seatNumber);
        assertThat(concertSeat.getStatus()).isEqualTo(status);
    }
}