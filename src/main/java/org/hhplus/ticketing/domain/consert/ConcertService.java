package org.hhplus.ticketing.domain.consert;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.consert.model.*;
import org.hhplus.ticketing.domain.consert.model.enums.ReservationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 콘서트 관련 비즈니스 로직을 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ConcertService {

    private static final Logger log = LoggerFactory.getLogger(ConcertService.class);

    private final ConcertRepository concertRepository;

    /**
     * 특정 콘서트에 대해 예약 가능한 날짜를 조회합니다.
     *
     * @param concertId 조회할 콘서트의 고유 ID
     * @return 예약 가능한 날짜 목록을 포함한 result 객체
     */
    @Transactional(readOnly = true)
    public ConcertResult.DatesForReservationResult getDatesForReservation(Long concertId) {
        return ConcertResult.DatesForReservationResult.from(concertRepository.findByConcertIdAndConcertAtAfter(concertId, LocalDateTime.now()));
    }

    /**
     * 특정 콘서트 옵션에 대해 예약 가능한 좌석을 조회합니다.
     *
     * @param concertOptionId 조회할 콘서트옵션의 고유 ID
     * @return 예약 가능한 좌석 목록을 포함한 result 객체
     */
    @Transactional(readOnly = true)
    public ConcertResult.SeatsForReservationResult getSeatsForReservation(Long concertOptionId) {
        return ConcertResult.SeatsForReservationResult.from(concertRepository.findByConcertOptionIdAndStatus(concertOptionId));
    }

    /**
     * 특정 콘서트 옵션의 좌석을 예약합니다.
     *
     * @param command 좌석 예약 요청 command 객체
     * @return 좌석 예약 정보를 포함한 result 객체
     */
    @Transactional
    public ConcertResult.ReserveSeatResult reserveSeat(ConcertCommand.ReserveSeatCommand command) {

        // 낙관적락 구현
        try {
            // 1. 좌석 정보 조회 (해당 좌석이 예약 가능한지) > 낙관적락
            // 리턴 결과 없을 시 > "좌석 정보를 찾을 수 없거나 이미 선점된 좌석입니다." 예외 리턴
            ConcertSeatDomain seatInfo = concertRepository.findAvailableSeat(command.getConcertSeatId()).orElseThrow(()
                    -> new RuntimeException("좌석 정보를 찾을 수 없거나 이미 선점된 좌석입니다."));

            // 2. 좌석 임시 배정 (약 5분) > 예약시간 + 5분 체크해서 좌석 만료 스케줄러 작업
            // (사용가능 > 예약됨)
            seatInfo.updateSeatReserved();
            concertRepository.saveSeat(seatInfo);

            ReservationDomain reservation = ReservationDomain.builder()
                    .concertSeatId(command.getConcertSeatId())
                    .userId(command.getUserId())
                    .status(ReservationStatus.RESERVED)
                    .build();

            return ConcertResult.ReserveSeatResult.from(concertRepository.saveReservation(reservation));

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("낙관적락 동시성 오류", e);
        }
    }

    /**
     * 예약 정보를 조회합니다.
     *
     * @param reservationId 조회할 예약 고유 ID
     * @return 예약정보 result 객체
     */
    public ConcertResult.GetReservationInfoResult getReservationInfo(Long reservationId) {
        // 예약 시점을 기준으로 만료되지 않은 예약건 조회
        // 조회결과 없을 시 "예약 정보를 찾을 수 없거나 이미 만료된 예약입니다." 예외 리턴
        return ConcertResult.GetReservationInfoResult.from(concertRepository.findByReservationIdAndStatus(reservationId, ReservationStatus.RESERVED).orElseThrow(()
                -> new RuntimeException("예약 정보를 찾을 수 없거나 이미 만료된 예약입니다.")));
    }


    /**
     * 좌석 소유권을 배정합니다.
     *
     * @param reservationId 소유권 배정할 예약 고유 ID
     * @param concertSeatId 소유권 배정할 좌석 고유 ID
     * @return 좌석 소유권을 배정 정보를 포함한 result 객체
     */
    @Transactional
    public ConcertResult.AssignSeatOwnershipResult assignSeatOwnership(Long reservationId, Long concertSeatId) {

        // 예약 정보 갱신
        // (예약됨 > 점유)
        int updateCnt = concertRepository.updateReservationStatus(reservationId, ReservationStatus.OCCUPIED);

        if (updateCnt == 0) {
            throw new RuntimeException("예약 정보 갱신 중에 예기치 못한 오류가 발생하였습니다.");
        }

        ConcertSeatDomain seatInfo = concertRepository.findAvailableSeat(concertSeatId).orElseThrow(()
                -> new RuntimeException("좌석 정보를 찾을 수 없습니다."));

        // 좌석 소유권 배정
        // (예약됨 > 점유)
        seatInfo.updateSeatOccupied();
        return ConcertResult.AssignSeatOwnershipResult.from(concertRepository.saveSeat(seatInfo));
    }

    /**
     * 임시예약 만료된 좌석정보를 되돌립니다. (스케줄러 2분 주기 작업)
     * > 임시예약은 5분간 유효 합니다
     * 1. 예약한지 5분이 경과한 예약건을 만료시킵니다.
     * 2. 만료된 예약건에 대한 좌석을 사용가능상태로 되돌립니다.
     */
    @Transactional
    public void releaseTemporaryReservations() {

        // 1. 예약 후 5분 경과했는데, 상태가 예약중인 리스트 조회 합니다.
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<ReservationDomain> reservationToExpire = concertRepository.findReservedBefore(fiveMinutesAgo);

        // 2. 예약 상태 만료로 세팅 합니다.
        // (예약됨 > 만료)
        List<ReservationDomain> expiredReservation = reservationToExpire.stream()
                .map(ReservationDomain::updateReservationExpired)
                .collect(Collectors.toList());

        // 3. 예약 정보를 갱신합니다.
        concertRepository.saveAllReservation(expiredReservation);

        // 4. 좌석 상태를 업데이트합니다.
        // (예약됨 > 사용가능)
        seatsToAvailable(expiredReservation);
    }

    /**
     * 만료된 예약건에 대한 좌석을 사용가능상태로 되돌립니다.
     */
    private void seatsToAvailable(List<ReservationDomain> expiredReservation) {

        // 1. 갱신된 예약정보에서 좌석ID를 추출합니다.
        List<Long> seatIdsToRelease = expiredReservation.stream()
                .map(ReservationDomain::getConcertSeatId)
                .collect(Collectors.toList());

        // 2. 대상 좌석ID를 조회합니다.
        List<ConcertSeatDomain> seatsToAvailable = concertRepository.findByConcertSeatIdIn(seatIdsToRelease);

        // 3. 좌석 상태를 사용가능으로 세팅합니다.
        // (예약됨 > 사용가능)
        List<ConcertSeatDomain> updatedSeats = seatsToAvailable.stream()
                .map(ConcertSeatDomain::updateSeatAvailable)
                .collect(Collectors.toList());

        // 4. 좌석 정보를 갱신합니다.
        concertRepository.saveAllSeat(updatedSeats);
    }
}
