package org.hhplus.ticketing.interfaces.controller.concert;

import org.hhplus.ticketing.application.concert.ConcertFacade;
import org.hhplus.ticketing.domain.concert.model.*;
import org.hhplus.ticketing.interfaces.controller.concert.dto.request.ConcertRequest;
import org.hhplus.ticketing.interfaces.controller.concert.dto.response.ConcertResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

// 콘서트 컨트롤러 단위테스트입니다.
public class ConcertControllerUnitTest {

    @InjectMocks
    private ConcertController concertController;

    @Mock
    private ConcertFacade concertFacade;

    private Long userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
    }

    @Test
    @DisplayName("🟢 콘서트_목록_조회_컨트롤러_테스트_예상_리턴_확인")
    void getConcertListTest_콘서트_목록_조회_컨트롤러_테스트_예상_리턴_확인() {

        // Given
        List<Concert> concertList = Arrays.asList(
                new Concert(1L, "콘서트1")
        );

        Page<Concert> concerts = new PageImpl<>(concertList, PageRequest.of(0, 20), concertList.size());

        List<ConcertResult.GetConcertListResult> result = concerts.stream()
                .map(ConcertResult.GetConcertListResult::from)
                .collect(Collectors.toList());

        Page<ConcertResult.GetConcertListResult> resultPage = new PageImpl<>(result, PageRequest.of(0, 20), result.size());

        List<ConcertResponse.GetConcertListResponse> response = resultPage.stream()
                .map(ConcertResponse.GetConcertListResponse::from)
                .collect(Collectors.toList());
        Page<ConcertResponse.GetConcertListResponse> responsePage = new PageImpl<>(response, PageRequest.of(0, 20), response.size());

        given(concertFacade.getConcertList(PageRequest.of(0, 20))).willReturn(resultPage);

        // When
        ResponseEntity<Page<ConcertResponse.GetConcertListResponse>> responseEntity = concertController.getConcertList(PageRequest.of(0, 20));

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseEntity.getBody(), responsePage);
    }

    @Test
    @DisplayName("🟢 예약_가능한_날짜_조회_컨트롤러_테스트_예상_리턴_확인")
    void getAvailableDatesTest_예약_가능한_날짜_조회_컨트롤러_테스트_예상_리턴_확인() throws Exception {

        // Given
        Long concertId = 1L;
        List<ConcertOption> concertOptions = Arrays.asList(
                new ConcertOption(1L, concertId, LocalDateTime.now().plusDays(15), 50),
                new ConcertOption(2L, concertId, LocalDateTime.now().plusDays(13), 50)
        );

        ConcertResult.GetAvailableDatesResult result = ConcertResult.GetAvailableDatesResult.from(concertOptions);
        ConcertResponse.GetAvailableDatesResponse response = ConcertResponse.GetAvailableDatesResponse.from(result);

        given(concertFacade.getAvailableDates(concertId)).willReturn(result);

        // When
        ResponseEntity<ConcertResponse.GetAvailableDatesResponse> responseEntity = concertController.getAvailableDates(concertId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

    @Test
    @DisplayName("🟢 좌석_예약_컨트롤러_테스트_예상_리턴_확인")
    void getAvailableSeatsTest_좌석_예약_컨트롤러_테스트_예상_리턴_확인() throws Exception {
        // Given
        Long concertOptionId = 1L;
        List<ConcertSeat> seats = Arrays.asList(
                ConcertSeat.builder()
                        .concertSeatId(1L)
                        .concertOptionId(concertOptionId)
                        .seatNumber(1)
                        .grade(ConcertSeat.Grade.VIP)
                        .price(ConcertSeat.Grade.VIP.getPrice())
                        .status(ConcertSeat.Status.AVAILABLE)
                        .build(),
                ConcertSeat.builder()
                        .concertSeatId(2L)
                        .concertOptionId(concertOptionId)
                        .seatNumber(2)
                        .grade(ConcertSeat.Grade.REGULAR)
                        .price(ConcertSeat.Grade.REGULAR.getPrice())
                        .status(ConcertSeat.Status.AVAILABLE)
                        .build()
        );

        ConcertResult.GetAvailableSeatsResult result = ConcertResult.GetAvailableSeatsResult.from(seats);
        ConcertResponse.GetAvailableSeatsResponse response = ConcertResponse.GetAvailableSeatsResponse.from(result);

        given(concertFacade.getAvailableSeats(concertOptionId)).willReturn(result);

        // When
        ResponseEntity<ConcertResponse.GetAvailableSeatsResponse> responseEntity = concertController.getAvailableSeats(concertOptionId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

    @Test
    @DisplayName("🟢 좌석_예약_컨트롤러_테스트_예상_리턴_확인")
    void reserveSeatTest_좌석_예약_컨트롤러_테스트_예상_리턴_확인 () throws Exception {
        // Given
        Long concertSeatId = 1L;
        ConcertRequest.ReserveSeatRequest request = new ConcertRequest.ReserveSeatRequest(userId, concertSeatId);
        ConcertResult.ReserveSeatResult result = new ConcertResult.ReserveSeatResult(1L, userId, concertSeatId);
        ConcertResponse.ReserveSeatResponse response = ConcertResponse.ReserveSeatResponse.from(result);

        given(concertFacade.reserveSeat(any(ConcertCommand.ReserveSeatCommand.class))).willReturn(result);

        // When
        ResponseEntity<ConcertResponse.ReserveSeatResponse> responseEntity = concertController.reserveSeat(request);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }
}