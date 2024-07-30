package org.hhplus.ticketing.domain.concert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.concert.model.constants.ConcertConstants;

import java.time.LocalDateTime;

/**
 * 도메인 객체: Reservation
 * 예약 정보 관리.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    private Long reservationId;          // 예약ID (키값)
    private Long concertSeatId;          // 콘서트좌석ID
    private Long userId;                 // 유저ID
    private LocalDateTime reservationAt; // 예약시간
    private int price;                   // 예약가격
    private Status status;               // 예약상태 (예약됨[RESERVED]/점유[OCCUPIED]/만료[EXPIRED])
    private Long version;                // 낙관적 락 버전 필드

    public static Reservation create(Long concertSeatId, Long userId, int price) {
        return Reservation.builder()
                .concertSeatId(concertSeatId)
                .userId(userId)
                .reservationAt(LocalDateTime.now())
                .price(price)
                .status(Status.RESERVED)
                .build();
    }

    public Reservation setExpired() {
        if (this.status != Status.RESERVED) throw new CustomException(ErrorCode.INVALID_STATE);
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(ConcertConstants.RESERVATION_EXPIRATION_MINUTES);
        if (!this.reservationAt.isBefore(expirationTime)) throw new CustomException(ErrorCode.INVALID_STATE);

        this.status = Status.EXPIRED;
        return this;
    }

    public Reservation setOccupied() {
        if (this.status != Status.RESERVED) throw new CustomException(ErrorCode.INVALID_STATE);

        this.status = Status.OCCUPIED;
        return this;
    }

    public enum Status {
        RESERVED,       // 예약됨
        OCCUPIED,       // 점유
        EXPIRED         // 만료
    }
}