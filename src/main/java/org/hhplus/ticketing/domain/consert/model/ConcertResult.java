package org.hhplus.ticketing.domain.consert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ConcertResult {

    // 특정 콘서트에 대해 예약 가능한 날짜 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DatesForReservationResult {
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

        public static DatesForReservationResult from(List<ConcertOptionDomain> domains) {
            if (domains == null || domains.isEmpty()) {
                return new DatesForReservationResult();
            }

            List<DateInfo> availableDates = domains.stream()
                    .map(domain -> DateInfo.builder()
                            .concertOptionId(domain.getConcertOptionId())
                            .concertAt(domain.getConcertAt().toLocalDate())
                            .build())
                    .collect(Collectors.toList());

            return DatesForReservationResult.builder()
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
    public static class SeatsForReservationResult {
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

        public static SeatsForReservationResult from(List<ConcertSeatDomain> domains) {
            if (domains == null || domains.isEmpty()) {
                return new SeatsForReservationResult();
            }

            List<SeatsForReservationResult.SeatInfo> availableSeats = domains.stream()
                    .map(domain -> SeatInfo.builder()
                            .concertSeatId(domain.getConcertSeatId())
                            .seatNumber(domain.getSeatNumber())
                            .build())
                    .collect(Collectors.toList());

            return SeatsForReservationResult.builder()
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

        public static ConcertResult.ReserveSeatResult from(ReservationDomain domain) {
            return ConcertResult.ReserveSeatResult.builder()
                    .reservationId(domain.getReservationId())
                    .userId(domain.getUserId())
                    .concertSeatId(domain.getConcertSeatId())
                    .build();
        }
    }

    // 예약 정보 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetReservationInfoResult {
        private Long reservationId;          // 예약ID (키값)
        private Long concertSeatId;          // 콘서트좌석ID
        private Long userId;                 // 유저ID
        private LocalDateTime reservationAt; // 예약시간

        public static ConcertResult.GetReservationInfoResult from(ReservationDomain domain) {
            return ConcertResult.GetReservationInfoResult.builder()
                    .reservationId(domain.getReservationId())
                    .concertSeatId(domain.getConcertSeatId())
                    .userId(domain.getUserId())
                    .reservationAt(domain.getReservationAt())
                    .build();
        }
    }

    // 좌석 소유권 배정 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignSeatOwnershipResult {
        private Long concertSeatId;          // 콘서트좌석ID

        public static ConcertResult.AssignSeatOwnershipResult from(ConcertSeatDomain domain) {
            return ConcertResult.AssignSeatOwnershipResult.builder()
                    .concertSeatId(domain.getConcertSeatId())
                    .build();
        }
    }
}
