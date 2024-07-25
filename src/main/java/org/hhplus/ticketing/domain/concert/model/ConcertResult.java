package org.hhplus.ticketing.domain.concert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ConcertResult {

    // 특정 콘서트에 대해 예약 가능한 날짜 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAvailableDatesResult {
        private Long concertId;                    // 콘서트ID
        private List<DateInfo> availableDates;     // 예약 가능한 날짜 리스트

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class DateInfo {
            private Long concertOptionId;
            private LocalDate concertAt;
        }

        public static GetAvailableDatesResult from(List<ConcertOption> domains) {
            if (domains == null || domains.isEmpty()) {
                return new GetAvailableDatesResult();
            }

            List<DateInfo> availableDates = domains.stream()
                    .map(domain -> DateInfo.builder()
                            .concertOptionId(domain.getConcertOptionId())
                            .concertAt(domain.getConcertAt().toLocalDate())
                            .build())
                    .collect(Collectors.toList());

            return GetAvailableDatesResult.builder()
                    .concertId(domains.get(0).getConcertId())
                    .availableDates(availableDates)
                    .build();
        }
    }

    // 특정 콘서트에 대해 예약 가능한 좌석 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAvailableSeatsResult {
        private Long concertOptionId;                // 콘서트옵션ID
        private List<SeatInfo> availableSeats;       // 예약가능한 좌석 리스트

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SeatInfo {
            private Long concertSeatId;
            private int seatNumber;
        }

        public static GetAvailableSeatsResult from(List<ConcertSeat> domains) {
            if (domains == null || domains.isEmpty()) {
                return new GetAvailableSeatsResult();
            }

            List<GetAvailableSeatsResult.SeatInfo> availableSeats = domains.stream()
                    .map(domain -> SeatInfo.builder()
                            .concertSeatId(domain.getConcertSeatId())
                            .seatNumber(domain.getSeatNumber())
                            .build())
                    .collect(Collectors.toList());

            return GetAvailableSeatsResult.builder()
                    .concertOptionId(domains.get(0).getConcertOptionId())
                    .availableSeats(availableSeats)
                    .build();
        }
    }

    // 특정 콘서트에 대해 예약 가능한 좌석 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReserveSeatResult {
        private Long reservationId;             // 예약ID
        private Long userId;                    // 유저ID
        private Long concertSeatId;             // 콘서트좌석ID

        public static ConcertResult.ReserveSeatResult from(Reservation domain) {
            return ConcertResult.ReserveSeatResult.builder()
                    .reservationId(domain.getReservationId())
                    .userId(domain.getUserId())
                    .concertSeatId(domain.getConcertSeatId())
                    .build();
        }
    }

    // 좌석 소유권 배정 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignSeatResult {
        private Long concertSeatId;          // 콘서트좌석ID
        private int price;                   // 좌석가격

        public static AssignSeatResult from(ConcertSeat domain) {
            return AssignSeatResult.builder()
                    .concertSeatId(domain.getConcertSeatId())
                    .price(domain.getPrice())
                    .build();
        }
    }
}
