package org.hhplus.ticketing.presentation.consert.controller;

import org.hhplus.ticketing.presentation.consert.dto.response.DatesForReservationResponse;
import org.hhplus.ticketing.presentation.consert.dto.response.SeatsForReservationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/concerts")
public class ConsertController {

    private static final Logger log = LoggerFactory.getLogger(ConsertController.class);

    /**
     * 특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.
     *
     * @param concertId 조회할 콘서트의 고유 ID
     * @return 예약 가능한 날짜 목록을 포함한 응답 객체
     */
    @GetMapping("/{concertId}/dates-for-reservation")
    public ResponseEntity<DatesForReservationResponse> getDatesForReservation (@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long concertId) {

        String token = authorizationHeader.replace("Bearer ", "");

        List<LocalDate> availableDates = Arrays.asList(
                LocalDate.of(2024, 7, 15),
                LocalDate.of(2024, 7, 16)
        );
        DatesForReservationResponse response = new DatesForReservationResponse(concertId, availableDates);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 콘서트 옵션에 대해 예약 가능한 좌석을 조회합니다.
     *
     * @param concertOptionId 조회할 콘서트의 고유 ID
     * @return 예약 가능한 좌석 목록을 포함한 응답 객체
     */
    @GetMapping("/{concertOptionId}/seats-for-reservation")
    public ResponseEntity<SeatsForReservationResponse> getSeatsForReservation (@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long concertOptionId) {

        String token = authorizationHeader.replace("Bearer ", "");

        // 좌석 정보는 1 ~ 50 까지의 좌석번호로 관리
        int[] availableSeats = {3, 4, 9, 15, 30};
        SeatsForReservationResponse response = new SeatsForReservationResponse(concertOptionId, availableSeats);
        return ResponseEntity.ok(response);
    }

}
