package org.hhplus.ticketing.domain.concert.model;

import lombok.*;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;

/**
 * 도메인 객체: ConcertSeat
 * 콘서트 좌석에 대한 기본 정보를 관리합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertSeat {
    private Long concertSeatId;        // 콘서트좌석ID
    private Long concertOptionId;      // 콘서트옵션ID
    private int seatNumber;            // 좌석번호 (1~50)
    private Grade grade;               // 좌석등급
    private int price;                 // 좌석가격
    private Status status;             // 좌석상태 (사용가능[AVAILABLE]/예약됨[RESERVED]/점유[OCCUPIED])
    private Long version;              // 낙관적 락 버전 필드

    public static ConcertSeat create(Long concertOptionId, int seatNumber, Grade grade) {
        return ConcertSeat.builder()
                .concertOptionId(concertOptionId)
                .seatNumber(seatNumber)
                .grade(grade)
                .price(grade.getPrice())
                .status(Status.AVAILABLE)
                .build();
    }

    public ConcertSeat setAvailable() {
        if (this.status != Status.RESERVED) throw new CustomException(ErrorCode.INVALID_STATE);

        this.status = Status.AVAILABLE;
        return this;
    }

    public ConcertSeat setReserved() {
        if (this.status != Status.AVAILABLE) throw new CustomException(ErrorCode.INVALID_STATE);

        this.status = Status.RESERVED;
        return this;
    }

    public ConcertSeat setOccupied() {
        if (this.status != Status.RESERVED) throw new CustomException(ErrorCode.INVALID_STATE);

        this.status = Status.OCCUPIED;
        return this;
    }

    @Getter
    public enum Grade {
        VIP(100000),
        PREMIUM(80000),
        REGULAR(50000),
        ECONOMY(30000);

        private final int price;

        Grade(int price) {
            this.price = price;
        }
    }

    public enum Status {
        AVAILABLE,     // 사용가능
        RESERVED,      // 예약됨
        OCCUPIED;      // 점유
    }
}