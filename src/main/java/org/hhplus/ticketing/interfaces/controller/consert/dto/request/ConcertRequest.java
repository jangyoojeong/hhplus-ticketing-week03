package org.hhplus.ticketing.interfaces.controller.consert.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hhplus.ticketing.domain.consert.model.ConcertCommand;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;

public class ConcertRequest {

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
