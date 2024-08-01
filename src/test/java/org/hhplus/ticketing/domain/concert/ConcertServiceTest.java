package org.hhplus.ticketing.domain.concert;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.concert.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

// 콘서트 서비스 단위테스트입니다.
public class ConcertServiceTest {

    @InjectMocks
    private ConcertService concertService;
    @Mock
    private ConcertRepository concertRepository;

    private ConcertSeat seat;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        seat = ConcertSeat.builder()
                .concertSeatId(1L)
                .concertOptionId(1L)
                .seatNumber(1)
                .grade(ConcertSeat.Grade.VIP)
                .price(ConcertSeat.Grade.VIP.getPrice())
                .status(ConcertSeat.Status.AVAILABLE)
                .build();
        reservation = Reservation.builder()
                .reservationId(1L)
                .concertSeatId(1L)
                .userId(1L)
                .reservationAt(LocalDateTime.now())
                .price(50000)
                .status(Reservation.Status.RESERVED)
                .build();
    }

    @Test
    @DisplayName("🟢 예약_가능한_날짜_조회_테스트")
    void getAvailableDatesTest_예약_가능한_날짜_조회_테스트() {

        // Given
        List<ConcertOption> concertOptions = Arrays.asList(
                new ConcertOption(1L, 1L, LocalDateTime.now().plusDays(1), 50),
                new ConcertOption(2L, 1L, LocalDateTime.now().plusDays(2), 50)
        );
        given(concertRepository.getAvailableDates(anyLong(), any(LocalDateTime.class))).willReturn(concertOptions);

        // When
        ConcertResult.GetAvailableDatesResult result = concertService.getAvailableDates(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getConcertId());
        assertEquals(2, result.getAvailableDates().size());
    }

    @Test
    @DisplayName("🟢 예약_가능한_좌석_조회_테스트")
    void getAvailableSeatsTest_예약_가능한_좌석_조회_테스트() {

        // Given
        List<ConcertSeat> concertSeats = Collections.singletonList(seat);
        given(concertRepository.getAvailableSeats(anyLong())).willReturn(concertSeats);

        // When
        ConcertResult.GetAvailableSeatsResult result = concertService.getAvailableSeats(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getConcertOptionId());
        assertEquals(1, result.getAvailableSeats().size());
    }

    @Test
    @DisplayName("🟢 좌석_예약_테스트_정상적으로_예약_성공")
    void reserveSeatTest_좌석_예약_테스트_정상적으로_예약_성공() {

        // Given
        ConcertCommand.ReserveSeatCommand command = new ConcertCommand.ReserveSeatCommand(1L, 1L);
        given(concertRepository.getAvailableSeat(anyLong())).willReturn(Optional.of(seat));
        given(concertRepository.saveSeat(any(ConcertSeat.class))).willReturn(seat);
        given(concertRepository.saveReservation(any(Reservation.class))).willReturn(reservation);

        // When
        ConcertResult.ReserveSeatResult result = concertService.reserveSeat(command);

        // Then
        assertNotNull(result);
        verify(concertRepository, times(1)).saveSeat(any(ConcertSeat.class));
        verify(concertRepository, times(1)).saveReservation(any(Reservation.class));
    }

    @Test
    @DisplayName("🔴 좌석_예약_테스트_해당_좌석이_예약가능한_상태가_아닐_경우_SEAT_NOT_FOUND_OR_ALREADY_RESERVED_예외반환")
    void reserveSeatTest_좌석_예약_테스트_해당_좌석이_예약가능한_상태가_아닐_경우_SEAT_NOT_FOUND_OR_ALREADY_RESERVED_예외반환() {

        // Given
        ConcertCommand.ReserveSeatCommand command = new ConcertCommand.ReserveSeatCommand(1L, 1L);
        given(concertRepository.getAvailableSeat(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> concertService.reserveSeat(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SEAT_NOT_FOUND_OR_ALREADY_RESERVED);
    }

    @Test
    @DisplayName("🟢 예약_정보_조회_테스트_예약_정보를_성공적으로_조회")
    void getReservationTest_예약_정보_조회_테스트_예약_정보를_성공적으로_조회() {

        // Given
        Long reservationId = 1L;
        given(concertRepository.getActiveReservation(anyLong())).willReturn(Optional.of(reservation));

        // When
        Reservation reservation = concertService.getReservation(reservationId);

        // Then
        assertNotNull(reservation);
        assertEquals(reservationId, reservation.getReservationId());
        verify(concertRepository, times(1)).getActiveReservation(reservationId);
    }

    @Test
    @DisplayName("🔴 예약_정보_조회_테스트_예약_정보_찾을_수_없거나_예약_만료시_RESERVATION_NOT_FOUND_예외반환")
    void getReservationInfoTest_예약_정보_조회_테스트_예약_정보_찾을_수_없거나_예약_만료시_RESERVATION_NOT_FOUND_예외반환() {

        // Given
        Long reservationId = 1L;

        given(concertRepository.getActiveReservation(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> concertService.getReservation(reservationId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESERVATION_NOT_FOUND);
    }


    @Test
    @DisplayName("🟢 좌석_소유권_배정_테스트")
    void assignSeatTest_좌석_소유권_배정_테스트() {

        // Given
        Long reservationId = 1L;
        Long concertSeatId = 1L;

        seat.setReserved();

        given(concertRepository.getActiveReservation(anyLong())).willReturn(Optional.of(reservation));
        given(concertRepository.saveReservation(any(Reservation.class))).willReturn(reservation);
        given(concertRepository.findSeatById(anyLong())).willReturn(Optional.of(seat));
        given(concertRepository.saveSeat(any(ConcertSeat.class))).willReturn(seat);

        // When
        ConcertResult.AssignSeatResult result = concertService.assignSeat(reservationId);

        // Then
        assertNotNull(result);
        assertThat(result.getConcertSeatId()).isEqualTo(concertSeatId);
    }

    @Test
    @DisplayName("🔴 좌석_소유권_배정_테스트_좌석_정보를_찾을_수_없을_때_INVALID_SEAT_SELECTION_예외반환")
    void assignSeatOwnershipTest_좌석_소유권_배정_테스트_좌석_정보를_찾을_수_없을_때_INVALID_SEAT_SELECTION_예외반환() {

        // Given
        Long reservationId = 1L;

        given(concertRepository.getActiveReservation(anyLong())).willReturn(Optional.of(reservation));
        given(concertRepository.saveReservation(any(Reservation.class))).willReturn(reservation);
        given(concertRepository.findSeatById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> concertService.assignSeat(reservationId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_SEAT_SELECTION);
    }
    @Test
    @DisplayName("🟢 임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보가_없으면_바로_종료된다")
    void releaseReservationsTest_임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보가_없으면_바로_종료된다() {

        // Given
        given(concertRepository.getExpiredReservations(any(LocalDateTime.class))).willReturn(Collections.emptyList());

        // When
        concertService.releaseReservations();

        // Then
        verify(concertRepository, times(1)).getExpiredReservations(any(LocalDateTime.class));
        verify(concertRepository, never()).saveAllReservation(anyList());
    }

    @Test
    @DisplayName("🟢 임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보가_있으면_저장로직이_정상적으로_실행된다")
    void releaseReservationsTest_임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보가_있으면_저장로직이_정상적으로_실행된다() {

        // Given
        Reservation reservation = mock(Reservation.class);
        List<Reservation> reservationToExpire = List.of(reservation);
        
        given(concertRepository.getExpiredReservations(any(LocalDateTime.class))).willReturn(reservationToExpire);
        given(reservation.setExpired()).willReturn(reservation);

        // When
        concertService.releaseReservations();

        // Then
        verify(concertRepository, times(1)).getExpiredReservations(any(LocalDateTime.class));
        verify(concertRepository, times(1)).saveAllReservation(reservationToExpire);
    }

    @Test
    @DisplayName("🟢 임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보를_통해_좌석상태도_갱신한다")
    void releaseReservationsTest_임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보를_통해_좌석상태도_갱신한다() {

        // Given
        Reservation reservation = mock(Reservation.class);
        ConcertSeat concertseat = mock(ConcertSeat.class);
        List<Reservation> expiredReservations = List.of(reservation);
        List<ConcertSeat> seats = List.of(concertseat);

        given(concertRepository.getExpiredReservations(any(LocalDateTime.class))).willReturn(expiredReservations);
        given(reservation.setExpired()).willReturn(reservation);
        given(reservation.getConcertSeatId()).willReturn(1L);
        given(concertRepository.getSeats(anyList())).willReturn(seats);
        given(concertseat.setAvailable()).willReturn(concertseat);

        // When
        concertService.releaseReservations();

        // Then
        verify(concertRepository, times(1)).getExpiredReservations(any(LocalDateTime.class));
        verify(concertRepository, times(1)).saveAllReservation(expiredReservations);
        verify(concertRepository, times(1)).saveAllSeat(seats);
    }
}