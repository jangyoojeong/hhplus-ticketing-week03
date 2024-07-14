package org.hhplus.ticketing.interfaces.controller.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.annotation.CheckToken;
import org.hhplus.ticketing.application.concert.facade.ConcertFacade;
import org.hhplus.ticketing.interfaces.controller.concert.dto.request.ConcertRequest;
import org.hhplus.ticketing.interfaces.controller.concert.dto.response.ConcertResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 콘서트 관련 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts")
@Tag(name = "Concert API", description = "콘서트 관련 API")
public class ConcertController {

    private static final Logger log = LoggerFactory.getLogger(ConcertController.class);

    private final ConcertFacade concertFacade;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * 특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.
     *
     * @param authorizationHeader 인증 토큰을 포함한 헤더
     * @param concertId 조회할 콘서트의 고유 ID
     * @return 예약 가능한 날짜 목록을 포함한 응답 객체
     */
    @CheckToken
    @GetMapping("/{concertId}/dates-for-reservation")
    @Operation(summary = "예약 가능 날짜 조회 API", description = "특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.")
    public ResponseEntity<ConcertResponse.DatesForReservationResponse> getDatesForReservation (@RequestHeader(value = AUTHORIZATION_HEADER, required = true) String authorizationHeader, @PathVariable Long concertId) {
            ConcertResponse.DatesForReservationResponse response = ConcertResponse.DatesForReservationResponse.from(concertFacade.getDatesForReservation(concertId));
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 특정 콘서트 옵션에 대해 예약 가능한 좌석을 조회합니다.
     *
     * @param authorizationHeader 인증 토큰을 포함한 헤더
     * @param concertOptionId 조회할 콘서트의 고유 ID
     * @return 예약 가능한 좌석 목록을 포함한 응답 객체
     */
    @CheckToken
    @GetMapping("/{concertOptionId}/seats-for-reservation")
    @Operation(summary = "예약 가능 좌석 조회 API", description = "특정 콘서트 옵션에 대해 예약 가능한 좌석을 조회합니다.")
    public ResponseEntity<ConcertResponse.SeatsForReservationResponse> getSeatsForReservation (@RequestHeader(value = AUTHORIZATION_HEADER, required = true) String authorizationHeader, @PathVariable Long concertOptionId) {
        ConcertResponse.SeatsForReservationResponse response = ConcertResponse.SeatsForReservationResponse.from(concertFacade.getSeatsForReservation(concertOptionId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 특정 콘서트 옵션의 좌석을 예약합니다.
     *
     * @param authorizationHeader 인증 토큰을 포함한 헤더
     * @param request 좌석 예약 요청 객체
     * @return 예약 결과 정보를 담은 객체
     */
    @CheckToken
    @PostMapping("/reservations/seats")
    @Operation(summary = "좌석 예약 요청 API", description = "특정 콘서트 옵션의 좌석을 예약합니다.")
    public ResponseEntity<ConcertResponse.ReserveSeatResponse> reserveSeat (@RequestHeader(value = AUTHORIZATION_HEADER, required = true) String authorizationHeader, @RequestBody ConcertRequest.ReserveSeatRequest request) {
        ConcertResponse.ReserveSeatResponse response = ConcertResponse.ReserveSeatResponse.from(concertFacade.reserveSeat(request.toCommand()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
