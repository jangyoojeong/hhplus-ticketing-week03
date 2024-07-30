package org.hhplus.ticketing.interfaces.controller.concert.dto.response;

import lombok.*;
import org.hhplus.ticketing.domain.concert.model.ConcertResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConcertResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConcertResponse {

        private Long concertId;             // 콘서트ID
        private String concertName;         // 콘서트명

        public static SaveConcertResponse from(ConcertResult.SaveConcertResult result) {
            return SaveConcertResponse.builder()
                    .concertId(result.getConcertId())
                    .concertName(result.getConcertName())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConcertOptionResponse {
        private Long concertId;             // 콘서트ID
        private LocalDateTime concertAt;    // 콘서트 시간
        private int capacity;               // 콘서트 정원

        public static SaveConcertOptionResponse from(ConcertResult.SaveConcertOptionResult result) {
            return SaveConcertOptionResponse.builder()
                    .concertId(result.getConcertId())
                    .concertAt(result.getConcertAt())
                    .capacity(result.getCapacity())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetConcertListResponse {

        private Long concertId;             // 콘서트ID
        private String concertName;         // 콘서트명

        public static ConcertResponse.GetConcertListResponse from(ConcertResult.GetConcertListResult result) {
            return GetConcertListResponse.builder()
                    .concertId(result.getConcertId())
                    .concertName(result.getConcertName())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAvailableDatesResponse {

        private Long concertId;                     // 콘서트ID
        private List<ConcertResult.GetAvailableDatesResult.DateInfo> availableDates;     // 예약 가능한 날짜

        public static GetAvailableDatesResponse from(ConcertResult.GetAvailableDatesResult result) {
            List<ConcertResult.GetAvailableDatesResult.DateInfo> availableDates =
                    Optional.ofNullable(result.getAvailableDates())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(dateInfo -> ConcertResult.GetAvailableDatesResult.DateInfo.builder()
                                    .concertOptionId(dateInfo.getConcertOptionId())
                                    .concertAt(dateInfo.getConcertAt())
                                    .build())
                            .collect(Collectors.toList());

            return GetAvailableDatesResponse.builder()
                    .concertId(result.getConcertId())
                    .availableDates(availableDates)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAvailableSeatsResponse {

        private Long concertOptionId;       // 콘서트옵션ID  
        private List<ConcertResult.GetAvailableSeatsResult.SeatInfo> availableSeats;     // 예약 가능한 좌석 리스트

        public static GetAvailableSeatsResponse from(ConcertResult.GetAvailableSeatsResult result) {
            List<ConcertResult.GetAvailableSeatsResult.SeatInfo> availableSeats =
                    Optional.ofNullable(result.getAvailableSeats())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(seatInfo -> ConcertResult.GetAvailableSeatsResult.SeatInfo.builder()
                                    .concertSeatId(seatInfo.getConcertSeatId())
                                    .seatNumber(seatInfo.getSeatNumber())
                                    .build())
                            .collect(Collectors.toList());

            return GetAvailableSeatsResponse.builder()
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
                    .reservationId(result.getReservationId())
                    .userId(result.getUserId())
                    .concertSeatId(result.getConcertSeatId())
                    .build();
        }
    }
}
