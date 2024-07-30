package org.hhplus.ticketing.interfaces.controller.concert.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.concert.model.ConcertCommand;

import java.time.LocalDateTime;

public class ConcertRequest {

    // 콘서트 등록 request
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConcertRequest {

        @NotNull(message = "콘서트 이름은 비어 있을 수 없습니다.")
        private String concertName;                    // 콘서트 이름

        public ConcertCommand.SaveConcertCommand toCommand() {
            return ConcertCommand.SaveConcertCommand
                    .builder()
                    .concertName(this.getConcertName())
                    .build();
        }
    }

    // 콘서트 옵션 등록 request
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConcertOptionRequest {

        @NotNull(message = "콘서트 ID는 비어 있을 수 없습니다.")
        private Long concertId;             // 콘서트ID

        @NotNull(message = "콘서트 시간은 비어 있을 수 없습니다.")
        private LocalDateTime concertAt;    // 콘서트 시간

        @NotNull(message = "콘서트 정원은 비어 있을 수 없습니다.")
        @Min(value = 1, message = "콘서트 정원은 최소 1명 이상이어야 합니다.")
        private int capacity;           // 콘서트 정원

        public ConcertCommand.SaveConcertOptionCommand toCommand() {
            return ConcertCommand.SaveConcertOptionCommand.builder()
                    .concertId(this.getConcertId())
                    .concertAt(this.getConcertAt())
                    .capacity(this.getCapacity())
                    .build();
        }
    }

    // 좌석 예약 request
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReserveSeatRequest {

        @NotNull(message = "사용자 ID는 비어 있을 수 없습니다.")
        private Long userId;                    // 유저ID

        @NotNull(message = "콘서트좌석ID는 비어 있을 수 없습니다.")
        private Long concertSeatId;             // 콘서트좌석ID

        public ConcertCommand.ReserveSeatCommand toCommand() {
            return ConcertCommand.ReserveSeatCommand
                    .builder()
                    .userId(this.getUserId())
                    .concertSeatId(this.getConcertSeatId())
                    .build();
        }
    }

}
