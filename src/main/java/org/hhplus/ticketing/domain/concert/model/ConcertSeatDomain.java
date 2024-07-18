package org.hhplus.ticketing.domain.concert.model;

import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.concert.model.enums.SeatStatus;

/**
 * 도메인 객체: ConcertSeat
 * 콘서트 좌석에 대한 기본 정보를 관리합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertSeatDomain {
    private Long concertSeatId;        // 콘서트좌석ID
    private Long concertOptionId;      // 콘서트옵션ID
    private int seatNumber;            // 좌석번호 (1~50)
    private SeatStatus status;         // 좌석상태 (사용가능[AVAILABLE]/예약됨[RESERVED]/점유[OCCUPIED])
    private Long version;              // 낙관적 락 버전 필드

    public ConcertSeatDomain(Long concertSeatId, Long concertOptionId, int seatNumber, SeatStatus status) {
        this.concertSeatId = concertSeatId;
        this.concertOptionId = concertOptionId;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    public ConcertSeatDomain updateSeatAvailable() {
        this.status = SeatStatus.AVAILABLE;
        return this;
    }

    public ConcertSeatDomain updateSeatReserved() {
        this.status = SeatStatus.RESERVED;
        return this;
    }

    public ConcertSeatDomain updateSeatOccupied() {
        this.status = SeatStatus.OCCUPIED;
        return this;
    }

}