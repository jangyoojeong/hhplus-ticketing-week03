package org.hhplus.ticketing.domain.concert;

import org.hhplus.ticketing.domain.concert.model.Concert;
import org.hhplus.ticketing.domain.concert.model.ConcertOption;
import org.hhplus.ticketing.domain.concert.model.ConcertSeat;
import org.hhplus.ticketing.domain.concert.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository {

    // ****************** ConcertDomain 관련 메서드 ******************

    /**
     * 콘서트 목록을 조회합니다.
     *
     * @return domain 저장된 콘서트 정보
     */
    Page<Concert> getConcertList(Pageable pageable);


    /**
     * 콘서트 정보를 저장합니다.
     *
     * @param domain 저장할 콘서트 정보
     * @return domain 저장된 콘서트 정보
     */
    Concert saveConcert(Concert domain);

    // ****************** ConcertOptionDomain 관련 메서드 ******************

    /**
     * 예약 가능한 콘서트 옵션을 조회합니다.
     *
     * @param concertId 조회 대상 concertId
     * @param currentDateTime 콘서트 시작일 이후 필터링 {@link LocalDateTime}
     * @return 조회된 콘서트 옵션 리스트
     */
    List<ConcertOption> getAvailableDates(Long concertId, LocalDateTime currentDateTime);

    /**
     * 콘서트 옵션 정보를 저장합니다.
     *
     * @param domain 저장할 콘서트 옵션 정보
     * @return domain 저장된 콘서트 옵션 정보
     */
    ConcertOption saveConcertOption(ConcertOption domain);

    // ****************** Concertseat 관련 메서드 ******************

    /**
     * 좌석상태를 갱신합니다.
     *
     * @param domains 저장할 예약 정보 리스트
     * @return 저장된 예약 정보 리스트
     */
    List<ConcertSeat> saveAllSeat(List<ConcertSeat> domains);

    /**
     * 갱신대상 좌석리스트를 조회합니다.
     *
     * @param concertSeatIds 조회할 좌석ID 리스트
     * @return 저장된 예약 정보 리스트
     */
    List<ConcertSeat> getSeats(List<Long> concertSeatIds);

    /**
     * 좌석정보를 조회합니다.
     *
     * @param concertSeatId 조회할 콘서트좌석ID
     * @return domain 조회된 콘서트좌석 정보
     */
    Optional<ConcertSeat> findSeatById(Long concertSeatId);

    /**
     * 예약가능한 좌석정보를 조회합니다.
     *
     * @param concertSeatId 조회할 콘서트좌석ID
     * @return domain 조회된 콘서트좌석 정보
     */
    Optional<ConcertSeat> getAvailableSeat(Long concertSeatId);

    /**
     * 특정 콘서트옵션ID에 대해 예약가능한 좌석리스트를 조회합니다.
     *
     * @param concertOptionId 조회할 콘서트옵션ID
     * @return domain 조회된 콘서트좌석 정보
     */
    List<ConcertSeat> getAvailableSeats(Long concertOptionId);

    /**
     * 좌석정보를 저장합니다.
     *
     * @param domain 저장할 예약 정보
     * @return domain 저장된 예약 정보
     */
    ConcertSeat saveSeat(ConcertSeat domain);

    // ****************** reservation 관련 메서드 ******************

    /**
     * 예약정보를 체크합니다 (만료 여부)
     *
     * @param reservationId 조회할 예약ID
     * @return domain 조회된 예약 정보
     */
    Optional<Reservation> getActiveReservation(Long reservationId);

    /**
     * 예약정보를 저장합니다.
     *
     * @param domain 저장할 예약 정보
     * @return domain 저장된 예약 정보
     */
    Reservation saveReservation(Reservation domain);

    /**
     * 만료대상 예약정보를 조회 합니다.
     *
     * @param time 특정 시간 이전에 예약된 정보를 필터링하기 위한 {@link LocalDateTime}
     * @return domain 조회된 예약 정보
     */
    List<Reservation> getExpiredReservations(LocalDateTime time);

    /**
     * 예약상태를 갱신합니다.
     *
     * @param domains 저장할 예약 정보 리스트
     * @return 저장된 예약 정보 리스트
     */
    List<Reservation> saveAllReservation(List<Reservation> domains);

    /**
     * 특정 사용자의 예약정보를 조회합니다.
     *
     * @param userId 예약 정보를 조회할 사용자 ID
     * @return 조회된 예약 정보 리스트
     */
    List<Reservation> findByUserId(Long userId);

    /**
     * 특정 좌석의 예약 정보를 조회합니다.
     *
     * @param concertSeatId 예약 정보를 조회할 좌석 ID
     * @return 조회된 예약 정보 리스트
     */
    List<Reservation> findByConcertSeatId(Long concertSeatId);

    /**
     * 예약정보를 조회합니다.
     *
     * @param reservationId 조회할 예약ID
     * @return 조회된 예약 정보
     */
    Optional<Reservation> findReservationById(Long reservationId);

}
