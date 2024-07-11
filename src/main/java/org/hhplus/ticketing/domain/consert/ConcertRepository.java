package org.hhplus.ticketing.domain.consert;

import org.hhplus.ticketing.domain.consert.model.ConcertDomain;
import org.hhplus.ticketing.domain.consert.model.ConcertOptionDomain;
import org.hhplus.ticketing.domain.consert.model.ConcertSeatDomain;
import org.hhplus.ticketing.domain.consert.model.ReservationDomain;
import org.hhplus.ticketing.domain.consert.model.enums.ReservationStatus;
import org.hhplus.ticketing.domain.queue.model.QueueDomain;
import org.hhplus.ticketing.infra.consert.entity.ConcertOption;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository {

    /**
     * 좌석정보를 조회합니다.
     *
     * @param concertSeatId 조회할 콘서트좌석ID
     * @return domain 조회된 콘서트좌석 정보
     */
    Optional<ConcertSeatDomain> findAvailableSeat(Long concertSeatId);

    /**
     * 특정 콘서트옵션ID에 대해 예약가능한 좌석리스트를 조회합니다.
     *
     * @param concertOptionId 조회할 콘서트옵션ID
     * @return domain 조회된 콘서트좌석 정보
     */
    List<ConcertSeatDomain> findByConcertOptionIdAndStatus(Long concertOptionId);


    /**
     * 좌석정보를 저장합니다.
     *
     * @param domain 저장할 예약 정보
     * @return domain 저장된 예약 정보
     */
    ConcertSeatDomain saveSeat(ConcertSeatDomain domain);

    /**
     * 예약정보를 체크합니다 (만료 여부)
     *
     * @param reservationId 조회할 예약ID
     * @return domain 조회된 예약 정보
     */
    Optional<ReservationDomain> findByReservationIdAndStatus(Long reservationId, ReservationStatus status);

    /**
     * 예약정보를 저장합니다.
     *
     * @param domain 저장할 예약 정보
     * @return domain 저장된 예약 정보
     */
    ReservationDomain saveReservation(ReservationDomain domain);

    /**
     * 만료대상 예약정보를 조회 합니다.
     *
     * @param time 특정 시간 이전에 예약된 정보를 필터링하기 위한 {@link LocalDateTime}
     * @return domain 조회된 예약 정보
     */
    List<ReservationDomain> findReservedBefore(LocalDateTime time);

    /**
     * 예약상태를 갱신합니다.
     *
     * @param reservationId 갱신할 예약 ID
     * @return 갱신된 건수
     */
    int updateReservationStatus(Long reservationId, ReservationStatus status);

    /**
     * 예약상태를 갱신합니다.
     *
     * @param domains 저장할 예약 정보 리스트
     * @return 저장된 예약 정보 리스트
     */
    List<ReservationDomain> saveAllReservation(List<ReservationDomain> domains);

    /**
     * 좌석상태를 갱신합니다.
     *
     * @param domains 저장할 예약 정보 리스트
     * @return 저장된 예약 정보 리스트
     */
    List<ConcertSeatDomain> saveAllSeat(List<ConcertSeatDomain> domains);

    /**
     * 갱신대상 좌석리스트를 조회합니다.
     *
     * @param concertSeatIds 조회할 좌석ID 리스트
     * @return 저장된 예약 정보 리스트
     */
    List<ConcertSeatDomain> findByConcertSeatIdIn(List<Long> concertSeatIds);

    /**
     * 예약 가능한 콘서트 옵션을 조회합니다.
     *
     * @param concertId 조회 대상 concertId
     * @param currentDateTime 콘서트 시작일 이후 필터링 {@link LocalDateTime}
     * @return 조회된 콘서트 옵션 리스트
     */
    List<ConcertOptionDomain> findByConcertIdAndConcertAtAfter(Long concertId, LocalDateTime currentDateTime);


    /**
     * 콘서트 정보를 저장합니다.
     *
     * @param domain 저장할 콘서트 정보
     * @return domain 저장된 콘서트 정보
     */
    ConcertDomain saveConcert(ConcertDomain domain);

    /**
     * 콘서트 옵션 정보를 저장합니다.
     *
     * @param domain 저장할 콘서트 옵션 정보
     * @return domain 저장된 콘서트 옵션 정보
     */
    ConcertOptionDomain saveConcertOption(ConcertOptionDomain domain);

}
