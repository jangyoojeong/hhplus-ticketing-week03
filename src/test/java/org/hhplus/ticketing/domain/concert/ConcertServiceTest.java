package org.hhplus.ticketing.domain.concert;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.concert.model.*;
import org.hhplus.ticketing.domain.concert.model.enums.ReservationStatus;
import org.hhplus.ticketing.domain.concert.model.enums.SeatStatus;
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

    private ConcertSeatDomain concertSeatDomain;
    private ReservationDomain reservationDomain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        concertSeatDomain = ConcertSeatDomain.builder()
                .concertSeatId(1L)
                .concertOptionId(1L)
                .seatNumber(1)
                .status(SeatStatus.AVAILABLE)
                .build();

        reservationDomain = ReservationDomain.builder()
                .reservationId(1L)
                .concertSeatId(1L)
                .userId(1L)
                .status(ReservationStatus.RESERVED)
                .build();
    }

    @Test
    @DisplayName("[성공테스트] 예약_가능한_날짜_조회_테스트")
    void getDatesForReservationTest_예약_가능한_날짜_조회_테스트() {

        // Given
        List<ConcertOptionDomain> concertOptions = Arrays.asList(
                new ConcertOptionDomain(1L, 1L, LocalDateTime.now().plusDays(1), 50),
                new ConcertOptionDomain(2L, 1L, LocalDateTime.now().plusDays(2), 50)
        );
        given(concertRepository.findByConcertIdAndConcertAtAfter(anyLong(), any(LocalDateTime.class))).willReturn(concertOptions);

        // When
        ConcertResult.DatesForReservationResult result = concertService.getDatesForReservation(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getConcertId());
        assertEquals(2, result.getAvailableDates().size());
    }

    @Test
    @DisplayName("[성공테스트] 예약_가능한_좌석_조회_테스트")
    void getSeatsForReservationTest_예약_가능한_좌석_조회_테스트() {

        // Given
        List<ConcertSeatDomain> concertSeats = Collections.singletonList(concertSeatDomain);
        given(concertRepository.findByConcertOptionIdAndStatus(anyLong())).willReturn(concertSeats);

        // When
        ConcertResult.SeatsForReservationResult result = concertService.getSeatsForReservation(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getConcertOptionId());
        assertEquals(1, result.getAvailableSeats().size());
    }

    @Test
    @DisplayName("[성공테스트] 좌석_예약_테스트_정상적으로_예약_성공")
    void reserveSeatTest_좌석_예약_테스트_정상적으로_예약_성공() {

        // Given
        ConcertCommand.ReserveSeatCommand command = new ConcertCommand.ReserveSeatCommand(1L, 1L);
        given(concertRepository.findAvailableSeatById(anyLong())).willReturn(Optional.of(concertSeatDomain));
        given(concertRepository.saveSeat(any(ConcertSeatDomain.class))).willReturn(concertSeatDomain);
        given(concertRepository.saveReservation(any(ReservationDomain.class))).willReturn(reservationDomain);

        // When
        ConcertResult.ReserveSeatResult result = concertService.reserveSeat(command);

        // Then
        assertNotNull(result);
        verify(concertRepository, times(1)).saveSeat(any(ConcertSeatDomain.class));
        verify(concertRepository, times(1)).saveReservation(any(ReservationDomain.class));
    }

    @Test
    @DisplayName("[실패테스트] 좌석_예약_테스트_해당_좌석이_예약가능한_상태가_아닐_경우_SEAT_NOT_FOUND_OR_ALREADY_RESERVED_예외반환")
    void reserveSeatTest_좌석_예약_테스트_해당_좌석이_예약가능한_상태가_아닐_경우_SEAT_NOT_FOUND_OR_ALREADY_RESERVED_예외반환() {

        // Given
        ConcertCommand.ReserveSeatCommand command = new ConcertCommand.ReserveSeatCommand(1L, 1L);
        given(concertRepository.findAvailableSeatById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> concertService.reserveSeat(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SEAT_NOT_FOUND_OR_ALREADY_RESERVED);
    }

    @Test
    @DisplayName("[성공테스트] 예약_정보_조회_테스트_예약_정보를_성공적으로_조회")
    void getReservationInfoTest_예약_정보_조회_테스트_예약_정보를_성공적으로_조회() {

        // Given
        Long reservationId = 1L;
        given(concertRepository.findByReservationIdAndStatus(anyLong(), any(ReservationStatus.class))).willReturn(Optional.of(reservationDomain));

        // When
        ConcertResult.GetReservationInfoResult result = concertService.getReservationInfo(reservationId);

        // Then
        assertNotNull(result);
        assertEquals(reservationId, result.getReservationId());
        verify(concertRepository, times(1)).findByReservationIdAndStatus(reservationId, ReservationStatus.RESERVED);
    }

    @Test
    @DisplayName("[실패테스트] 예약_정보_조회_테스트_예약_정보_찾을_수_없거나_예약_만료시_RESERVATION_NOT_FOUND_예외반환")
    void getReservationInfoTest_예약_정보_조회_테스트_예약_정보_찾을_수_없거나_예약_만료시_RESERVATION_NOT_FOUND_예외반환() {

        // Given
        Long reservationId = 1L;
        given(concertRepository.findByReservationIdAndStatus(anyLong(), any(ReservationStatus.class))).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> concertService.getReservationInfo(reservationId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESERVATION_NOT_FOUND);
    }


    @Test
    @DisplayName("[성공테스트] 좌석_소유권_배정_테스트")
    void assignSeatOwnershipTest_좌석_소유권_배정_테스트() {

        // Given
        int updateCnt = 1;
        Long reservationId = 1L;
        Long concertSeatId = 1L;

        given(concertRepository.updateReservationStatus(anyLong(), any(ReservationStatus.class))).willReturn(updateCnt);
        ConcertSeatDomain seatInfo = ConcertSeatDomain.builder()
                .concertSeatId(concertSeatId)
                .concertOptionId(1L)
                .seatNumber(1)
                .status(SeatStatus.AVAILABLE)
                .build();
        given(concertRepository.findSeatById(anyLong())).willReturn(Optional.of(seatInfo));
        given(concertRepository.saveSeat(any(ConcertSeatDomain.class))).willReturn(seatInfo);

        // When
        ConcertResult.AssignSeatOwnershipResult result = concertService.assignSeatOwnership(reservationId, concertSeatId);

        // Then
        assertNotNull(result);
        verify(concertRepository, times(1)).updateReservationStatus(reservationId, ReservationStatus.OCCUPIED);
        verify(concertRepository, times(1)).findSeatById(concertSeatId);
        verify(concertRepository, times(1)).saveSeat(seatInfo);
    }

    @Test
    @DisplayName("[실패테스트] 좌석_소유권_배정_테스트_예약_정보_갱신_실패_RESERVATION_UPDATE_FAILED_예외반환")
    void assignSeatOwnershipTest_좌석_소유권_배정_테스트_예약_정보_갱신_실패_RESERVATION_UPDATE_FAILED_예외반환() {

        // Given
        int updateCnt = 0;
        Long reservationId = 1L;
        Long concertSeatId = 1L;

        given(concertRepository.updateReservationStatus(anyLong(), any(ReservationStatus.class))).willReturn(updateCnt);

        // When & Then
        assertThatThrownBy(() -> concertService.assignSeatOwnership(reservationId, concertSeatId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESERVATION_UPDATE_FAILED);
    }

    @Test
    @DisplayName("[실패테스트] 좌석_소유권_배정_테스트_좌석_정보를_찾을_수_없을_때_INVALID_SEAT_SELECTION_예외반환")
    void assignSeatOwnershipTest_좌석_소유권_배정_테스트_좌석_정보를_찾을_수_없을_때_INVALID_SEAT_SELECTION_예외반환() {

        // Given
        int updateCnt = 1;
        Long reservationId = 1L;
        Long concertSeatId = 1L;

        given(concertRepository.updateReservationStatus(anyLong(), any(ReservationStatus.class))).willReturn(updateCnt);
        given(concertRepository.findSeatById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> concertService.assignSeatOwnership(reservationId, concertSeatId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_SEAT_SELECTION);
    }
    @Test
    @DisplayName("[성공테스트] 임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보가_없으면_바로_종료된다")
    void releaseTemporaryReservationsTest_임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보가_없으면_바로_종료된다() {

        // Given
        given(concertRepository.findReservedBefore(any(LocalDateTime.class))).willReturn(Collections.emptyList());

        // When
        concertService.releaseTemporaryReservations();

        // Then
        verify(concertRepository, times(1)).findReservedBefore(any(LocalDateTime.class));
        verify(concertRepository, never()).saveAllReservation(anyList());
    }

    @Test
    @DisplayName("[성공테스트] 임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보가_있으면_저장로직이_정상적으로_실행된다")
    void releaseTemporaryReservationsTest_임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보가_있으면_저장로직이_정상적으로_실행된다() {

        // Given
        ReservationDomain reservationDomain = mock(ReservationDomain.class);
        List<ReservationDomain> reservationToExpire = List.of(reservationDomain);
        
        given(concertRepository.findReservedBefore(any(LocalDateTime.class))).willReturn(reservationToExpire);
        given(reservationDomain.updateReservationExpired()).willReturn(reservationDomain);

        // When
        concertService.releaseTemporaryReservations();

        // Then
        verify(concertRepository, times(1)).findReservedBefore(any(LocalDateTime.class));
        verify(concertRepository, times(1)).saveAllReservation(reservationToExpire);
    }

    @Test
    @DisplayName("[성공테스트] 임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보를_통해_좌석상태도_갱신한다")
    void releaseTemporaryReservationsTest_임시예약_만료된_좌석정보_되돌림_테스트_만료_대상_임시예약_정보를_통해_좌석상태도_갱신한다() {

        // Given
        ReservationDomain reservationDomain = mock(ReservationDomain.class);
        ConcertSeatDomain concertSeatDomain = mock(ConcertSeatDomain.class);
        List<ReservationDomain> reservationToExpire = List.of(reservationDomain);
        List<ConcertSeatDomain> seatsToAvailable = List.of(concertSeatDomain);

        given(concertRepository.findReservedBefore(any(LocalDateTime.class))).willReturn(reservationToExpire);
        given(reservationDomain.updateReservationExpired()).willReturn(reservationDomain);
        given(reservationDomain.getConcertSeatId()).willReturn(1L);
        given(concertRepository.findByConcertSeatIdIn(anyList())).willReturn(seatsToAvailable);
        given(concertSeatDomain.updateSeatAvailable()).willReturn(concertSeatDomain);

        // When
        concertService.releaseTemporaryReservations();

        // Then
        verify(concertRepository, times(1)).findReservedBefore(any(LocalDateTime.class));
        verify(concertRepository, times(1)).saveAllReservation(reservationToExpire);
        verify(concertRepository, times(1)).saveAllSeat(seatsToAvailable);
    }
}