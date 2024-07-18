package org.hhplus.ticketing.domain.concert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.concert.model.ConcertCommand;
import org.hhplus.ticketing.domain.concert.model.ConcertResult;
import org.hhplus.ticketing.domain.concert.model.ConcertSeatDomain;
import org.hhplus.ticketing.domain.concert.model.ReservationDomain;
import org.hhplus.ticketing.domain.concert.model.constants.ConcertConstants;
import org.hhplus.ticketing.domain.concert.model.enums.ReservationStatus;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
            ConcertSeatDomain seatInfo = concertRepository.findAvailableSeatById(command.getConcertSeatId()).orElseThrow(()
                    -> new CustomException(ErrorCode.SEAT_NOT_FOUND, ErrorCode.SEAT_NOT_FOUND.getMessage()));

            // 2. 좌석 임시 배정 (약 5분) > 예약시간 + 5분 체크해서 좌석 만료 스케줄러 작업
            // (사용가능 > 예약됨)
            seatInfo.updateSeatReserved();
            concertRepository.saveSeat(seatInfo);

            // 3. 예약 정보 생성
            // status > 예약됨
            ReservationDomain reservation = ReservationDomain.createReservation(command.getConcertSeatId(), command.getUserId());

            ConcertResult.ReserveSeatResult result = ConcertResult.ReserveSeatResult.from(concertRepository.saveReservation(reservation));

            return result;
        } catch (OptimisticLockingFailureException e) {
            log.error("이미 선점된 좌석입니다. 좌석 ID: {}", command.getConcertSeatId(), e);
            throw new CustomException(ErrorCode.CONFLICTING_RESERVATION, ErrorCode.CONFLICTING_RESERVATION.getMessage(), e);
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
                -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND, ErrorCode.RESERVATION_NOT_FOUND.getMessage())));
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
            log.warn("예약 정보 갱신 실패 - 예약 ID: {}", reservationId);
            throw new CustomException(ErrorCode.RESERVATION_UPDATE_FAILED, ErrorCode.RESERVATION_UPDATE_FAILED.getMessage());
        }

        ConcertSeatDomain seatInfo = concertRepository.findSeatById(concertSeatId).orElseThrow(()
                -> new CustomException(ErrorCode.INVALID_SEAT_SELECTION, ErrorCode.INVALID_SEAT_SELECTION.getMessage()));

        // 좌석 소유권 배정
        // (예약됨 > 점유)
        seatInfo.updateSeatOccupied();
        ConcertResult.AssignSeatOwnershipResult result = ConcertResult.AssignSeatOwnershipResult.from(concertRepository.saveSeat(seatInfo));

        log.info("좌석 소유권 배정 성공 - 예약 ID: {}, 좌석 ID: {}", reservationId, concertSeatId);

        return result;
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
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(ConcertConstants.RESERVATION_EXPIRATION_MINUTES);
        List<ReservationDomain> reservationToExpire = concertRepository.findReservedBefore(fiveMinutesAgo);

        if (reservationToExpire.isEmpty()) {
            log.info("만료 대상 임시예약 정보가 없습니다.");
            return;
        }

        // 2. 예약 상태 만료로 세팅 합니다.
        // (예약됨 > 만료)
        List<ReservationDomain> expiredReservation = reservationToExpire.stream()
                .map(ReservationDomain::updateReservationExpired)
                .collect(Collectors.toList());

        // 3. 예약 정보를 갱신합니다.
        concertRepository.saveAllReservation(expiredReservation);

        log.info("만료된 예약 정보 갱신 성공 - 총 {} 건", expiredReservation.size());

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

        log.info("만료된 좌석 상태 갱신 성공 - 총 {} 건", updatedSeats.size());
    }
}
