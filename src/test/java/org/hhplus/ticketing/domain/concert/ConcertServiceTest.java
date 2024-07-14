package org.hhplus.ticketing.domain.concert;

import org.hhplus.ticketing.domain.concert.model.*;
import org.hhplus.ticketing.domain.concert.model.enums.ReservationStatus;
import org.hhplus.ticketing.domain.concert.model.enums.SeatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        given(concertRepository.findAvailableSeat(anyLong())).willReturn(Optional.of(concertSeatDomain));
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
    @DisplayName("[실패테스트] 좌석_예약_테스트_해당_좌석이_예약가능한_상태가_아닐_경우_예외반환")
    void reserveSeatTest_좌석_예약_테스트_해당_좌석이_예약가능한_상태가_아닐_경우_예외반환() {

        // Given
        ConcertCommand.ReserveSeatCommand command = new ConcertCommand.ReserveSeatCommand(1L, 1L);
        given(concertRepository.findAvailableSeat(anyLong())).willReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> concertService.reserveSeat(command));
        assertEquals("좌석 정보를 찾을 수 없거나 이미 선점된 좌석입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("[실패테스트] 좌석_예약_테스트_좌석_예약_시_동시성_오류_예외반환")
    void reserveSeatTest_좌석_예약_테스트_좌석_예약_시_동시성_오류_예외반환() {

        // Given
        ConcertCommand.ReserveSeatCommand command = new ConcertCommand.ReserveSeatCommand(1L, 1L);
        given(concertRepository.findAvailableSeat(anyLong())).willReturn(Optional.of(concertSeatDomain));
        doThrow(OptimisticLockingFailureException.class).when(concertRepository).saveSeat(any(ConcertSeatDomain.class));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> concertService.reserveSeat(command));
        assertEquals("낙관적락 동시성 오류", exception.getMessage());
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
    @DisplayName("[실패테스트] 예약_정보_조회_테스트_예약_정보_찾을_수_없거나_예약_만료시_예외반환")
    void getReservationInfoTest_예약_정보_조회_테스트_예약_정보_찾을_수_없거나_예약_만료시_예외반환() {

        // Given
        Long reservationId = 1L;
        given(concertRepository.findByReservationIdAndStatus(anyLong(), any(ReservationStatus.class))).willReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> concertService.getReservationInfo(reservationId));
        assertEquals("예약 정보를 찾을 수 없거나 이미 만료된 예약입니다.", exception.getMessage());
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
        given(concertRepository.findAvailableSeat(anyLong())).willReturn(Optional.of(seatInfo));
        given(concertRepository.saveSeat(any(ConcertSeatDomain.class))).willReturn(seatInfo);

        // When
        ConcertResult.AssignSeatOwnershipResult result = concertService.assignSeatOwnership(reservationId, concertSeatId);

        // Then
        assertNotNull(result);
        verify(concertRepository, times(1)).updateReservationStatus(reservationId, ReservationStatus.OCCUPIED);
        verify(concertRepository, times(1)).findAvailableSeat(concertSeatId);
        verify(concertRepository, times(1)).saveSeat(seatInfo);
    }

    @Test
    @DisplayName("[실패테스트] 좌석_소유권_배정_테스트_예약_정보_갱신_실패_예외반환")
    void assignSeatOwnershipTest_좌석_소유권_배정_테스트_예약_정보_갱신_실패_예외반환() {

        // Given
        int updateCnt = 0;
        Long reservationId = 1L;
        Long concertSeatId = 1L;

        given(concertRepository.updateReservationStatus(anyLong(), any(ReservationStatus.class))).willReturn(updateCnt);

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> concertService.assignSeatOwnership(reservationId, concertSeatId));
        assertEquals("예약 정보 갱신 중에 예기치 못한 오류가 발생하였습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("[실패테스트] 좌석_소유권_배정_테스트_좌석_정보를_찾을_수_없을_때_예외반환")
    void assignSeatOwnershipTest_좌석_소유권_배정_테스트_좌석_정보를_찾을_수_없을_때_예외반환() {

        // Given
        int updateCnt = 1;
        Long reservationId = 1L;
        Long concertSeatId = 1L;

        given(concertRepository.updateReservationStatus(anyLong(), any(ReservationStatus.class))).willReturn(updateCnt);
        given(concertRepository.findAvailableSeat(anyLong())).willReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> concertService.assignSeatOwnership(reservationId, concertSeatId));
        assertEquals("좌석 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("[성공테스트] 임시예약_만료된_좌석정보_되돌림_테스트")
    void releaseTemporaryReservationsTest_임시예약_만료된_좌석정보_되돌림_테스트() {
        // Given
        // 예약 후 5분 경과했는데, 상태가 예약중인 리스트 만료 로직 (예약중 -> 만료)
        ReservationDomain reservation1 = ReservationDomain.builder()
                .concertSeatId(1L)
                .concertSeatId(1L)
                .userId(1L)
                .reservationAt(LocalDateTime.of(2024, 6, 30, 14, 30, 00))
                .status(ReservationStatus.RESERVED)
                .build();
        ReservationDomain reservation2 = ReservationDomain.builder()
                .concertSeatId(2L)
                .concertSeatId(2L)
                .userId(2L)
                .reservationAt(LocalDateTime.of(2024, 6, 30, 14, 30, 00))
                .status(ReservationStatus.RESERVED)
                .build();

        List<ReservationDomain> reservationsToExpire = Arrays.asList(reservation1, reservation2);

        given(concertRepository.findReservedBefore(any(LocalDateTime.class))).willReturn(reservationsToExpire);

        reservationsToExpire.forEach(reservation -> reservation.setStatus(ReservationStatus.EXPIRED));

        given(concertRepository.saveAllReservation(anyList())).willReturn(reservationsToExpire);

        // 좌석 상태 갱신 로직 (예약중 -> 사용가능)
        List<ConcertSeatDomain> seatsToUpdate = Arrays.asList(
                ConcertSeatDomain.builder().concertSeatId(1L).status(SeatStatus.RESERVED).build(),
                ConcertSeatDomain.builder().concertSeatId(2L).status(SeatStatus.RESERVED).build()
        );

        given(concertRepository.findByConcertSeatIdIn(anyList())).willReturn(seatsToUpdate);

        seatsToUpdate.forEach(concertSeat -> concertSeat.setStatus(SeatStatus.AVAILABLE));

        given(concertRepository.saveAllSeat(anyList())).willReturn(seatsToUpdate);

        // When
        concertService.releaseTemporaryReservations();

        // Then
        verify(concertRepository, times(1)).findReservedBefore(any(LocalDateTime.class));
        verify(concertRepository, times(1)).saveAllReservation(anyList());
        verify(concertRepository, times(1)).findByConcertSeatIdIn(anyList());
        verify(concertRepository, times(1)).saveAllSeat(anyList());
    }
}