package org.hhplus.ticketing.domain.consert.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.queue.model.QueueDomain;

import java.time.LocalDate;
import java.util.List;

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
