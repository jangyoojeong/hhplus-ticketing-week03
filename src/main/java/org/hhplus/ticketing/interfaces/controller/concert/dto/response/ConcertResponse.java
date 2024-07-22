package org.hhplus.ticketing.interfaces.controller.concert.dto.response;

import lombok.*;
import org.hhplus.ticketing.domain.concert.model.ConcertResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConcertResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getAvailableDatesResponse {

        private Long concertId;                     // 콘서트ID
        private List<ConcertResult.getAvailableDatesResult.DateInfo> availableDates;     // 예약 가능한 날짜

        public static getAvailableDatesResponse from(ConcertResult.getAvailableDatesResult result) {
            List<ConcertResult.getAvailableDatesResult.DateInfo> availableDates =
                    Optional.ofNullable(result.getAvailableDates())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(dateInfo -> ConcertResult.getAvailableDatesResult.DateInfo.builder()
                                    .concertOptionId(dateInfo.getConcertOptionId())
                                    .concertAt(dateInfo.getConcertAt())
                                    .build())
                            .collect(Collectors.toList());

            return getAvailableDatesResponse.builder()
                    .concertId(result.getConcertId())
                    .availableDates(availableDates)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getAvailableSeatsResponse {

        private Long concertOptionId;       // 콘서트옵션ID  
        private List<ConcertResult.getAvailableSeatsResult.SeatInfo> availableSeats;     // 예약 가능한 좌석 리스트

        public static getAvailableSeatsResponse from(ConcertResult.getAvailableSeatsResult result) {
            List<ConcertResult.getAvailableSeatsResult.SeatInfo> availableSeats =
                    Optional.ofNullable(result.getAvailableSeats())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(seatInfo -> ConcertResult.getAvailableSeatsResult.SeatInfo.builder()
                                    .concertSeatId(seatInfo.getConcertSeatId())
                                    .seatNumber(seatInfo.getSeatNumber())
                                    .build())
                            .collect(Collectors.toList());

            return getAvailableSeatsResponse.builder()
                    .concertOptionId(result.getConcertOptionId())
                    .availableSeats(availableSeats)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReserveSeatResponse {

        private Long reservationId;             // 예약ID
        private Long userId;                    // 유저ID
        private Long concertSeatId;             // 콘서트좌석ID

        public static ConcertResponse.ReserveSeatResponse from(ConcertResult.ReserveSeatResult result) {
            return ConcertResponse.ReserveSeatResponse.builder()
                    .build();
        }
    }
}
