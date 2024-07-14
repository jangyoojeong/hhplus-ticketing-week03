package org.hhplus.ticketing.domain.concert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ConcertCommand {

    // 좌석 예약 command
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReserveSeatCommand {

        private Long userId;                    // 유저ID
        private Long concertSeatId;             // 콘서트좌석ID

    }
}
