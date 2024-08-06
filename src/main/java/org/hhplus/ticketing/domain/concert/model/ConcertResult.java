package org.hhplus.ticketing.domain.concert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ConcertResult {

    // 콘서트 등록 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConcert {
        private Long concertId;             // 콘서트ID
        private String concertName;         // 콘서트명

        public static SaveConcert from(Concert domain) {
            return SaveConcert.builder()
                    .concertId(domain.getConcertId())
                    .concertName(domain.getConcertName())
                    .build();
        }
    }

    // 콘서트 옵션 등록 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConcertOption {
        private Long concertId;             // 콘서트ID
        private LocalDateTime concertAt;    // 콘서트 시간
        private int capacity;               // 콘서트 정원

        public static SaveConcertOption from(ConcertOption domain) {
            return SaveConcertOption.builder()
                    .concertId(domain.getConcertId())
                    .concertAt(domain.getConcertAt())
                    .capacity(domain.getCapacity())
                    .build();
        }
    }

    // 좌석 소유권 배정 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetConcertList implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long concertId;             // 콘서트ID
        private String concertName;         // 콘서트명

        public static GetConcertList from(Concert domain) {
            return GetConcertList.builder()
                    .concertId(domain.getConcertId())
                    .concertName(domain.getConcertName())
                    .build();
        }
    }

    // 특정 콘서트에 대해 예약 가능한 날짜 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAvailableDates implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long concertId;                    // 콘서트ID
        private List<DateInfo> availableDates;     // 예약 가능한 날짜 리스트

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class DateInfo implements Serializable {
            private static final long serialVersionUID = 1L;

            private Long concertOptionId;
            private LocalDate concertAt;
        }

        public static GetAvailableDates from(List<ConcertOption> domains) {
            if (domains == null || domains.isEmpty()) {
                return new GetAvailableDates();
            }

            List<DateInfo> availableDates = domains.stream()
                    .map(domain -> DateInfo.builder()
                            .concertOptionId(domain.getConcertOptionId())
                            .concertAt(domain.getConcertAt().toLocalDate())
                            .build())
                    .collect(Collectors.toList());

            return GetAvailableDates.builder()
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
    public static class GetAvailableSeats {
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

        public static GetAvailableSeats from(List<ConcertSeat> domains) {
            if (domains == null || domains.isEmpty()) {
                return new GetAvailableSeats();
            }

            List<GetAvailableSeats.SeatInfo> availableSeats = domains.stream()
                    .map(domain -> SeatInfo.builder()
                            .concertSeatId(domain.getConcertSeatId())
                            .seatNumber(domain.getSeatNumber())
                            .build())
                    .collect(Collectors.toList());

            return GetAvailableSeats.builder()
                    .concertOptionId(domains.get(0).getConcertOptionId())
                    .availableSeats(availableSeats)
                    .build();
        }
    }

    // 좌석 예약 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReserveSeat {
        private Long reservationId;             // 예약ID
        private Long userId;                    // 유저ID
        private Long concertSeatId;             // 콘서트좌석ID

        public static ReserveSeat from(Reservation domain) {
            return ReserveSeat.builder()
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
    public static class AssignSeat {
        private Long concertSeatId;          // 콘서트좌석ID
        private int price;                   // 좌석가격

        public static AssignSeat from(Reservation domain) {
            return AssignSeat.builder()
                    .concertSeatId(domain.getConcertSeatId())
                    .price(domain.getPrice())
                    .build();
        }
    }
}
