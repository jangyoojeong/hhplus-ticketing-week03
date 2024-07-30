package org.hhplus.ticketing.interfaces.controller.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.application.concert.ConcertFacade;
import org.hhplus.ticketing.interfaces.controller.concert.dto.request.ConcertRequest;
import org.hhplus.ticketing.interfaces.controller.concert.dto.response.ConcertResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final ConcertFacade concertFacade;

    /**
     * 콘서트 목록을 조회합니다.
     *
     * @return 콘서트 목록 응답 객체
     */
    @GetMapping("/")
    @Operation(summary = "콘서트 목록 조회 API", description = "콘서트 목록을 조회합니다.")
    public ResponseEntity<Page<ConcertResponse.GetConcertListResponse>> getConcertList(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(concertFacade.getConcertList(pageable)
                .map(ConcertResponse.GetConcertListResponse::from));
    }

    /**
     * 콘서트를 등록합니다.
     *
     * @param request 콘서트 등록 요청 객체
     * @return 등록된 콘서트 정보를 포함한 응답 객체
     */
    @PostMapping("/")
    @Operation(summary = "콘서트 등록 API", description = "콘서트를 등록합니다.")
    public ResponseEntity<ConcertResponse.SaveConcertResponse> saveConcert(@Valid @RequestBody ConcertRequest.SaveConcertRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(ConcertResponse.SaveConcertResponse.from(concertFacade.saveConcert(request.toCommand())));
    }

    /**
     * 콘서트 옵션을 등록합니다.
     *
     * @param request 콘서트 등록 요청 객체
     * @return 등록된 콘서트 정보를 포함한 응답 객체
     */
    @PostMapping("/{concertId}/")
    @Operation(summary = "콘서트 옵션 등록 API", description = "특정 콘서트에 대해 옵션정보를 등록합니다.")
    public ResponseEntity<ConcertResponse.SaveConcertOptionResponse> saveConcertOption(@Valid @RequestBody ConcertRequest.SaveConcertOptionRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(ConcertResponse.SaveConcertOptionResponse.from(concertFacade.saveConcertOption(request.toCommand())));
    }

    /**
     * 특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.
     *
     * @param concertId 조회할 콘서트의 고유 ID
     * @return 예약 가능한 날짜 목록을 포함한 응답 객체
     */
    @GetMapping("/{concertId}/available-dates")
    @Operation(summary = "예약 가능 날짜 조회 API", description = "특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.")
    public ResponseEntity<ConcertResponse.GetAvailableDatesResponse> getAvailableDates(@PathVariable Long concertId) {
            return ResponseEntity.status(HttpStatus.OK).body(ConcertResponse.GetAvailableDatesResponse.from(concertFacade.getAvailableDates(concertId)));
    }

    /**
     * 특정 콘서트 옵션에 대해 예약 가능한 좌석을 조회합니다.
     *
     * @param concertOptionId 조회할 콘서트의 고유 ID
     * @return 예약 가능한 좌석 목록을 포함한 응답 객체
     */
    @GetMapping("/{concertOptionId}/available-seats")
    @Operation(summary = "예약 가능 좌석 조회 API", description = "특정 콘서트 옵션에 대해 예약 가능한 좌석을 조회합니다.")
    public ResponseEntity<ConcertResponse.GetAvailableSeatsResponse> getAvailableSeats(@PathVariable Long concertOptionId) {
        return ResponseEntity.status(HttpStatus.OK).body(ConcertResponse.GetAvailableSeatsResponse.from(concertFacade.getAvailableSeats(concertOptionId)));
    }

    /**
     * 특정 콘서트 옵션의 좌석을 예약합니다.
     *
     * @param request 좌석 예약 요청 객체
     * @return 예약 결과 정보를 담은 객체
     */
    @PostMapping("/reservations/seats")
    @Operation(summary = "좌석 예약 요청 API", description = "특정 콘서트 옵션의 좌석을 예약합니다.")
    public ResponseEntity<ConcertResponse.ReserveSeatResponse> reserveSeat (@Valid @RequestBody ConcertRequest.ReserveSeatRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(ConcertResponse.ReserveSeatResponse.from(concertFacade.reserveSeat(request.toCommand())));
    }
}
