package org.hhplus.ticketing.application.concert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.concert.model.ConcertCommand;

import java.time.LocalDateTime;

public class ConcertCriteria {

    // 콘서트 등록 criteria
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConcert {

        private String concertName;                    // 콘서트 이름

        public ConcertCommand.SaveConcert toCommand() {
            return ConcertCommand.SaveConcert
                    .builder()
                    .concertName(this.getConcertName())
                    .build();
        }
    }

    // 콘서트 옵션 등록 criteria
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConcertOption {

        private Long concertId;             // 콘서트ID
        private LocalDateTime concertAt;    // 콘서트 시간
        private int capacity;               // 콘서트 정원

        public ConcertCommand.SaveConcertOption toCommand() {
            return ConcertCommand.SaveConcertOption.builder()
                    .concertId(this.getConcertId())
                    .concertAt(this.getConcertAt())
                    .capacity(this.getCapacity())
                    .build();
        }
    }

    // 좌석 예약 criteria
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReserveSeat {

        private Long userId;                    // 유저ID
        private Long concertSeatId;             // 콘서트좌석ID

        public ConcertCommand.ReserveSeat toCommand() {
            return ConcertCommand.ReserveSeat
                    .builder()
                    .userId(this.getUserId())
                    .concertSeatId(this.getConcertSeatId())
                    .build();
        }
    }
}
