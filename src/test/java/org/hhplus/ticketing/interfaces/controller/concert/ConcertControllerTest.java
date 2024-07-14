package org.hhplus.ticketing.interfaces.controller.concert;

import org.hhplus.ticketing.application.concert.facade.ConcertFacade;
import org.hhplus.ticketing.domain.concert.model.ConcertCommand;
import org.hhplus.ticketing.domain.concert.model.ConcertOptionDomain;
import org.hhplus.ticketing.domain.concert.model.ConcertResult;
import org.hhplus.ticketing.domain.concert.model.ConcertSeatDomain;
import org.hhplus.ticketing.domain.concert.model.enums.SeatStatus;
import org.hhplus.ticketing.interfaces.controller.concert.dto.request.ConcertRequest;
import org.hhplus.ticketing.interfaces.controller.concert.dto.response.ConcertResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

// 콘서트 컨트롤러 단위테스트입니다.
public class ConcertControllerTest {

    @InjectMocks
    private ConcertController concertController;

    @Mock
    private ConcertFacade concertFacade;

    private Long userId;
    private UUID token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        token = UUID.randomUUID();
    }

    @Test
    @DisplayName("[성공테스트] 예약_가능한_날짜_조회_컨트롤러_테스트_예상_리턴_확인")
    void getDatesForReservationTest_예약_가능한_날짜_조회_컨트롤러_테스트_예상_리턴_확인 () throws Exception {

        // Given
        Long concertId = 1L;
        List<ConcertOptionDomain> concertOptions = Arrays.asList(
                new ConcertOptionDomain(1L, concertId, LocalDateTime.of(2024, 7, 15, 18, 0), 50),
                new ConcertOptionDomain(2L, concertId, LocalDateTime.of(2024, 7, 16, 18, 0), 50)
        );

        ConcertResult.DatesForReservationResult result = ConcertResult.DatesForReservationResult.from(concertOptions);
        ConcertResponse.DatesForReservationResponse response = ConcertResponse.DatesForReservationResponse.from(result);

        given(concertFacade.getDatesForReservation(concertId)).willReturn(result);

        // When
        ResponseEntity<ConcertResponse.DatesForReservationResponse> responseEntity = concertController.getDatesForReservation("Bearer " + token.toString(), concertId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

    @Test
    @DisplayName("[성공테스트] 좌석_예약_컨트롤러_테스트_예상_리턴_확인")
    void getSeatsForReservationTest_좌석_예약_컨트롤러_테스트_예상_리턴_확인 () throws Exception {
        // Given
        Long concertOptionId = 1L;
        List<ConcertSeatDomain> concertSeat = Arrays.asList(
                new ConcertSeatDomain(1L, concertOptionId, 1, SeatStatus.AVAILABLE),
                new ConcertSeatDomain(2L, concertOptionId, 2, SeatStatus.AVAILABLE)
        );

        ConcertResult.SeatsForReservationResult result = ConcertResult.SeatsForReservationResult.from(concertSeat);
        ConcertResponse.SeatsForReservationResponse response = ConcertResponse.SeatsForReservationResponse.from(result);

        given(concertFacade.getSeatsForReservation(concertOptionId)).willReturn(result);

        // When
        ResponseEntity<ConcertResponse.SeatsForReservationResponse> responseEntity = concertController.getSeatsForReservation("Bearer " + token.toString(), concertOptionId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

    @Test
    @DisplayName("[성공테스트] 좌석_예약_컨트롤러_테스트_예상_리턴_확인")
    void reserveSeatTest_좌석_예약_컨트롤러_테스트_예상_리턴_확인 () throws Exception {
        // Given
        Long concertSeatId = 1L;
        ConcertRequest.ReserveSeatRequest request = new ConcertRequest.ReserveSeatRequest(userId, concertSeatId);
        ConcertResult.ReserveSeatResult result = new ConcertResult.ReserveSeatResult(1L, userId, concertSeatId);
        ConcertResponse.ReserveSeatResponse response = ConcertResponse.ReserveSeatResponse.from(result);

        given(concertFacade.reserveSeat(any(ConcertCommand.ReserveSeatCommand.class))).willReturn(result);

        // When
        ResponseEntity<ConcertResponse.ReserveSeatResponse> responseEntity = concertController.reserveSeat("Bearer " + token.toString(), request);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

}