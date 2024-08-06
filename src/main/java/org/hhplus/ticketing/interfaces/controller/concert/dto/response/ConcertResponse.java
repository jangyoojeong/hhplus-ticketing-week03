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
    public static class SaveConcert {

        private Long concertId;             // 콘서트ID
        private String concertName;         // 콘서트명

        public static SaveConcert from(ConcertResult.SaveConcert result) {
            return SaveConcert.builder()
                    .concertId(result.getConcertId())
                    .concertName(result.getConcertName())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConcertOption {
        private Long concertId;             // 콘서트ID
        private LocalDateTime concertAt;    // 콘서트 시간
        private int capacity;               // 콘서트 정원

        public static SaveConcertOption from(ConcertResult.SaveConcertOption result) {
            return SaveConcertOption.builder()
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
    public static class GetConcertList {

        private Long concertId;             // 콘서트ID
        private String concertName;         // 콘서트명

        public static GetConcertList from(ConcertResult.GetConcertList result) {
            return GetConcertList.builder()
                    .concertId(result.getConcertId())
                    .concertName(result.getConcertName())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAvailableDates {

        private Long concertId;                     // 콘서트ID
        private List<ConcertResult.GetAvailableDates.DateInfo> availableDates;     // 예약 가능한 날짜

        public static GetAvailableDates from(ConcertResult.GetAvailableDates result) {
            List<ConcertResult.GetAvailableDates.DateInfo> availableDates =
                    Optional.ofNullable(result.getAvailableDates())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(dateInfo -> ConcertResult.GetAvailableDates.DateInfo.builder()
                                    .concertOptionId(dateInfo.getConcertOptionId())
                                    .concertAt(dateInfo.getConcertAt())
                                    .build())
                            .collect(Collectors.toList());

            return GetAvailableDates.builder()
                    .concertId(result.getConcertId())
                    .availableDates(availableDates)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAvailableSeats {

        private Long concertOptionId;       // 콘서트옵션ID  
        private List<ConcertResult.GetAvailableSeats.SeatInfo> availableSeats;     // 예약 가능한 좌석 리스트

        public static GetAvailableSeats from(ConcertResult.GetAvailableSeats result) {
            List<ConcertResult.GetAvailableSeats.SeatInfo> availableSeats =
                    Optional.ofNullable(result.getAvailableSeats())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(seatInfo -> ConcertResult.GetAvailableSeats.SeatInfo.builder()
                                    .concertSeatId(seatInfo.getConcertSeatId())
                                    .seatNumber(seatInfo.getSeatNumber())
                                    .build())
                            .collect(Collectors.toList());

            return GetAvailableSeats.builder()
                    .concertOptionId(result.getConcertOptionId())
                    .availableSeats(availableSeats)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReserveSeat {

        private Long reservationId;             // 예약ID
        private Long userId;                    // 유저ID
        private Long concertSeatId;             // 콘서트좌석ID

        public static ReserveSeat from(ConcertResult.ReserveSeat result) {
            return ReserveSeat.builder()
                    .reservationId(result.getReservationId())
                    .userId(result.getUserId())
                    .concertSeatId(result.getConcertSeatId())
                    .build();
        }
    }
}
