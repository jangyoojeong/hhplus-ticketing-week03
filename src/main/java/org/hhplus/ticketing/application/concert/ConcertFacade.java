package org.hhplus.ticketing.application.concert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.concert.ConcertService;
import org.hhplus.ticketing.domain.concert.model.ConcertCommand;
import org.hhplus.ticketing.domain.concert.model.ConcertResult;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

/**
 * 콘서트 관련 비즈니스 로직을 캡슐화하는 파사드 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;

    /**
     * 특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.
     *
     * @param concertId 조회할 콘서트의 고유 ID
     * @return 예약 가능한 날짜 목록을 포함한 result 객체
     */
    public ConcertResult.GetAvailableDatesResult getAvailableDates(Long concertId) {
        return concertService.getAvailableDates(concertId);
    }

    /**
     * 특정 콘서트 옵션에 대해 예약 가능한 좌석을 조회합니다.
     *
     * @param concertOptionId 좌석 예약 요청 command 객체
     * @return 예약 가능한 좌석 목록을 포함한 result 객체
     */
    public ConcertResult.GetAvailableSeatsResult getAvailableSeats(Long concertOptionId) {
        return concertService.getAvailableSeats(concertOptionId);
    }

    /**
     * 특정 콘서트 옵션의 좌석을 예약합니다. (낙관적락)
     *
     * @param command 좌석 예약 요청 command 객체
     * @return 좌석 예약 정보를 포함한 result 객체
     */
    public ConcertResult.ReserveSeatResult reserveSeat(ConcertCommand.ReserveSeatCommand command) {
        try {
            return concertService.reserveSeat(command);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(ErrorCode.CONFLICTING_RESERVATION, e);
        }
    }

    /**
     * 2분 주기로 좌석 임시예약이 만료된 것을 처리합니다.
     * 1. 좌석 (예약됨 > 사용가능)
     */
    public void releaseReservations() {
        concertService.releaseReservations();
    }
}
