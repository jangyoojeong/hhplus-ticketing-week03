package org.hhplus.ticketing.presentation.reservation.controller;

import org.hhplus.ticketing.presentation.reservation.dto.request.ReserveSeatRequest;
import org.hhplus.ticketing.presentation.reservation.dto.response.ReserveSeatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    /**
     * 특정 콘서트 옵션의 좌석을 예약합니다.
     *
     * @param request 좌석 예약 요청 객체
     * @return 예약 결과 정보를 담은 객체
     */
    @PostMapping("/seats")
    public ResponseEntity<ReserveSeatResponse> reserveSeat (@RequestHeader("Authorization") String authorizationHeader, @RequestBody ReserveSeatRequest request) {

        String token = authorizationHeader.replace("Bearer ", "");

        ReserveSeatResponse response = new ReserveSeatResponse(780L, request.getUuid(), request.getConcertOptionId(), request.getSeatNumber());
        return ResponseEntity.ok(response);
    }
}
