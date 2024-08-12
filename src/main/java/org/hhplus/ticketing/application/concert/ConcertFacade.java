package org.hhplus.ticketing.application.concert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.concert.ConcertService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * 콘서트를 저장합니다.
     *
     * @param criteria 콘서트 저장 요청 criteria 객체
     * @return 저장된 콘서트 정보를 포함한 result 객체
     */
    public ConcertResult.SaveConcert saveConcert(ConcertCriteria.SaveConcert criteria) {
        return ConcertResult.SaveConcert.from(concertService.saveConcert(criteria.toCommand()));
    }

    /**
     * 콘서트 목록을 조회합니다.
     *
     * @return 콘서트 목록 응답 객체
     */
    public Page<ConcertResult.GetConcertList> getConcertList(Pageable pageable) {
        return concertService.getConcertList(pageable)
                .map(ConcertResult.GetConcertList::from);
    }

    /**
     * 콘서트 옵션을 저장합니다.
     *
     * @param criteria 콘서트 옵션 저장 요청 criteria 객체
     * @return 저장된 콘서트 정보를 포함한 result 객체
     */
    public ConcertResult.SaveConcertOption saveConcertOption(ConcertCriteria.SaveConcertOption criteria) {
        return ConcertResult.SaveConcertOption.from(concertService.saveConcertOption(criteria.toCommand()));
    }

    /**
     * 특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.
     *
     * @param concertId 조회할 콘서트의 고유 ID
     * @return 예약 가능한 날짜 목록을 포함한 result 객체
     */
    public ConcertResult.GetAvailableDates getAvailableDates(Long concertId) {
        return ConcertResult.GetAvailableDates.from(concertService.getAvailableDates(concertId));
    }

    /**
     * 특정 콘서트 옵션에 대해 예약 가능한 좌석을 조회합니다.
     *
     * @param concertOptionId 좌석 예약 요청 criteria 객체
     * @return 예약 가능한 좌석 목록을 포함한 result 객체
     */
    public ConcertResult.GetAvailableSeats getAvailableSeats(Long concertOptionId) {
        return ConcertResult.GetAvailableSeats.from(concertService.getAvailableSeats(concertOptionId));
    }

    /**
     * 특정 콘서트 옵션의 좌석을 예약합니다. (낙관적락)
     *
     * @param criteria 좌석 예약 요청 criteria 객체
     * @return 좌석 예약 정보를 포함한 result 객체
     */
    public ConcertResult.ReserveSeat reserveSeat(ConcertCriteria.ReserveSeat criteria) {
        return ConcertResult.ReserveSeat.from(concertService.reserveSeat(criteria.toCommand()));
    }

    /**
     * 2분 주기로 좌석 임시예약이 만료된 것을 처리합니다.
     * 1. 좌석 (예약됨 > 사용가능)
     */
    public void releaseReservations() {
        concertService.releaseReservations();
    }
}
