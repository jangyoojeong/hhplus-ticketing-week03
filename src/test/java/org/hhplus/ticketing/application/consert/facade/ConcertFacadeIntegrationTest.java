package org.hhplus.ticketing.application.consert.facade;

import org.hhplus.ticketing.domain.consert.ConcertService;
import org.hhplus.ticketing.domain.consert.model.ConcertCommand;
import org.hhplus.ticketing.domain.consert.model.ConcertResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

// 콘서트 파사드&서비스 통합테스트입니다.
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ConcertFacadeIntegrationTest {

    @Autowired
    private ConcertFacade concertFacade;

    @MockBean
    private ConcertService concertService;

    private ConcertResult.DatesForReservationResult datesForReservationResult;
    private ConcertResult.SeatsForReservationResult seatsForReservationResult;
    private ConcertResult.ReserveSeatResult reserveSeatResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        datesForReservationResult = new ConcertResult.DatesForReservationResult();
        seatsForReservationResult = new ConcertResult.SeatsForReservationResult();
        reserveSeatResult = new ConcertResult.ReserveSeatResult();
    }

    @Test
    @DisplayName("[성공테스트] 예약_가능한_날짜_조회_테스트")
    void getDatesForReservationTest_예약_가능한_날짜_조회_테스트() {
        // Given
        given(concertService.getDatesForReservation(anyLong())).willReturn(datesForReservationResult);

        // When
        ConcertResult.DatesForReservationResult result = concertFacade.getDatesForReservation(1L);

        // Then
        assertNotNull(result);
        verify(concertService, times(1)).getDatesForReservation(1L);
    }

    @Test
    @DisplayName("[성공테스트] 예약_가능한_좌석_조회_테스트")
    void getSeatsForReservationTest_예약_가능한_좌석_조회_테스트() {
        // Given
        given(concertService.getSeatsForReservation(anyLong())).willReturn(seatsForReservationResult);

        // When
        ConcertResult.SeatsForReservationResult result = concertFacade.getSeatsForReservation(1L);

        // Then
        assertNotNull(result);
        verify(concertService, times(1)).getSeatsForReservation(1L);
    }

    @Test
    @DisplayName("[성공테스트] 좌석_예약_테스트")
    void reserveSeatTest_좌석_예약_테스트() {
        // Given
        given(concertService.reserveSeat(any(ConcertCommand.ReserveSeatCommand.class))).willReturn(reserveSeatResult);

        // When
        ConcertResult.ReserveSeatResult result = concertFacade.reserveSeat(new ConcertCommand.ReserveSeatCommand());

        // Then
        assertNotNull(result);
        verify(concertService, times(1)).reserveSeat(any(ConcertCommand.ReserveSeatCommand.class));
    }

    @Test
    @DisplayName("[성공테스트] 임시_예약_만료_처리_테스트")
    void releaseTemporaryReservationsTest_임시_예약_만료_처리_테스트() {
        // Given
        doNothing().when(concertService).releaseTemporaryReservations();

        // When
        concertFacade.releaseTemporaryReservations();

        // Then
        verify(concertService, times(1)).releaseTemporaryReservations();
    }
}