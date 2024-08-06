package org.hhplus.ticketing.domain.concert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ConcertCommand {


    // 콘서트 등록 command
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConcert {

        private String concertName;                    // 콘서트 이름

    }

    // 콘서트 옵션 등록 command
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConcertOption {

        private Long concertId;             // 콘서트ID
        private LocalDateTime concertAt;    // 콘서트 시간
        private int capacity;               // 콘서트 정원

    }

    // 좌석 예약 command
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReserveSeat {

        private Long userId;                    // 유저ID
        private Long concertSeatId;             // 콘서트좌석ID

    }
}
