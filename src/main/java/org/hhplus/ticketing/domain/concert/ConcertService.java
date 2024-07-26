package org.hhplus.ticketing.domain.concert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.concert.model.ConcertCommand;
import org.hhplus.ticketing.domain.concert.model.ConcertResult;
import org.hhplus.ticketing.domain.concert.model.ConcertSeat;
import org.hhplus.ticketing.domain.concert.model.Reservation;
import org.hhplus.ticketing.domain.concert.model.constants.ConcertConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 콘서트 관련 비즈니스 로직을 담당하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    /**
     * 특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.
     *
     * @param concertId 조회할 콘서트의 고유 ID
     * @return 예약 가능한 날짜 목록을 포함한 result 객체
     */
    @Transactional(readOnly = true)
    public ConcertResult.GetAvailableDatesResult getAvailableDates(Long concertId) {
        return ConcertResult.GetAvailableDatesResult.from(concertRepository.getAvailableDates(concertId, LocalDateTime.now()));
    }

    /**
     * 특정 콘서트 옵션에 대해 예약 가능한 좌석을 조회합니다.
     *
     * @param concertOptionId 조회할 콘서트옵션의 고유 ID
     * @return 예약 가능한 좌석 목록을 포함한 result 객체
     */
    @Transactional(readOnly = true)
    public ConcertResult.GetAvailableSeatsResult getAvailableSeats(Long concertOptionId) {
        return ConcertResult.GetAvailableSeatsResult.from(concertRepository.getAvailableSeats(concertOptionId));
    }

    /**
     * 특정 콘서트 옵션의 좌석을 예약합니다.
     *
     * @param command 좌석 예약 요청 command 객체
     * @return 좌석 예약 정보를 포함한 result 객체
     * @throws CustomException 예약 가능한 좌석이 없거나 이미 선점된 경우 발생
     */
    @Transactional(rollbackFor = {Exception.class})
    public ConcertResult.ReserveSeatResult reserveSeat(ConcertCommand.ReserveSeatCommand command) {

        // 1. 좌석 정보 조회 (해당 좌석이 예약 가능한지) > 낙관적락
        // 리턴 결과 없을 시 > "좌석 정보를 찾을 수 없거나 이미 선점된 좌석입니다." 예외 리턴
        ConcertSeat seat = concertRepository.getAvailableSeat(command.getConcertSeatId()).orElseThrow(()
                -> new CustomException(ErrorCode.SEAT_NOT_FOUND_OR_ALREADY_RESERVED));
        seat.setReserved();
        concertRepository.saveSeat(seat);

        Reservation reservation = Reservation.create(command.getConcertSeatId(), command.getUserId(), seat.getPrice());
        return ConcertResult.ReserveSeatResult.from(concertRepository.saveReservation(reservation));
    }

    /**
     * 주어진 예약 ID로 만료되지 않은 예약을 조회합니다.
     *
     * @param reservationId 조회할 예약의 고유 ID
     * @return 만료되지 않은 예약 객체
     * @throws CustomException 예약이 존재하지 않거나 만료된 경우
     */
    public Reservation getReservation(Long reservationId) {
        return concertRepository.getActiveReservation(reservationId).orElseThrow(()
                -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    /**
     * 예약된 좌석의 소유권을 배정합니다.
     *
     * @param reservationId 소유권 배정할 예약 고유 ID
     * @return 좌석 소유권 배정 정보를 포함한 result 객체
     * @throws CustomException 예약 또는 좌석 정보가 유효하지 않은 경우 발생
     */
    @Transactional(rollbackFor = {Exception.class})
    public ConcertResult.AssignSeatResult assignSeat(Long reservationId) {

        Reservation reservation = getReservation(reservationId);
        reservation.setOccupied();
        concertRepository.saveReservation(reservation);

        ConcertSeat seat = concertRepository.findSeatById(reservation.getConcertSeatId()).orElseThrow(()
                -> new CustomException(ErrorCode.INVALID_SEAT_SELECTION));
        seat.setOccupied();

        return ConcertResult.AssignSeatResult.from(concertRepository.saveSeat(seat));
    }

    /**
     * 임시예약 만료된 좌석정보를 되돌립니다. (스케줄러 2분 주기 작업)
     * > 임시예약은 5분간 유효 합니다
     * 1. 예약한지 5분이 경과한 예약건을 만료시킵니다.
     * 2. 만료된 예약건에 대한 좌석을 사용가능상태로 되돌립니다.
     */
    @Transactional(rollbackFor = {Exception.class})
    public void releaseReservations() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(ConcertConstants.RESERVATION_EXPIRATION_MINUTES);
        List<Reservation> expiredReservations = concertRepository.getExpiredReservations(expirationTime);
        if (!expiredReservations.isEmpty()) {
            expiredReservations.forEach(Reservation::setExpired);
            concertRepository.saveAllReservation(expiredReservations);
            log.info("총 {}개의 예약이 만료되었습니다.", expiredReservations.size());
        }

        releaseSeats(expiredReservations);
    }

    /**
     * 만료된 예약건에 대한 좌석을 사용가능상태로 되돌립니다.
     */
    private void releaseSeats(List<Reservation> expiredReservation) {

        List<Long> seatIds = expiredReservation.stream()
                .map(Reservation::getConcertSeatId)
                .collect(Collectors.toList());

        List<ConcertSeat> seats = concertRepository.getSeats(seatIds);
        seats.forEach(ConcertSeat::setAvailable);
        concertRepository.saveAllSeat(seats);
        log.info("총 {}개의 좌석이 만료되었습니다.", seats.size());
    }
}
