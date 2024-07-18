package org.hhplus.ticketing.domain.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;
import org.hhplus.ticketing.domain.queue.model.QueueDomain;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;
import org.hhplus.ticketing.domain.queue.model.enums.TokenStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 대기열 관련 비즈니스 로직을 담당하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;

    /**
     * 콘서트 대기열에 입장할 때 사용하는 토큰을 발급합니다.
     * 사용자는 이 토큰을 통해 대기열에 대한 인증을 받을 수 있습니다.
     *
     * @param command 토큰 발급 요청 command 객체
     * @return 발급된 토큰과 대기열 정보를 포함한 result 객체
     */
    @Transactional
    public QueueResult.IssueTokenResult issueToken(QueueCommand.IssueTokenCommand command) {

        // 1. 대기열 객체 생성 (대기열 정보 확인 후 즉시입장 가능한지 확인하여 초기 객체 세팅)
        QueueDomain queueDomain = createQueue(command.getUserId());

        // 2. 토큰 저장 및 결과 반환
        return QueueResult.IssueTokenResult.from(queueRepository.save(queueDomain));
    }

    /**
     * 사용자의 대기열 상태(대기순번 등)를 반환합니다.
     *
     * @param token 대기열 상태 조회할 token
     * @return 대기순번 등 대기열 상태 result 객체
     * @throws CustomException 토큰 정보가 존재하지 않는 경우
     */
    public QueueResult.QueueStatusResult getQueueStatus(UUID token) {

        Long queuePosition;

        // 토큰 조회
        QueueDomain queueInfo = getQueueInfoByToken(token);

        // 대기순번 결정
        queuePosition = determineQueuePosition(queueInfo);

        return new QueueResult.QueueStatusResult(queueInfo.getUserId(), queueInfo.getToken(), queuePosition, queueInfo.getStatus());
    }

    /**
     * QueueDomain 객체의 대기순번을 결정합니다.
     *
     * @param queueInfo 대기열 정보를 담고 있는 QueueDomain 객체
     * @return 대기순번
     */
    private Long determineQueuePosition(QueueDomain queueInfo) {
        // 상태 != WAITING -> 대기순번 1 RETURN
        return (queueInfo.getStatus() != TokenStatus.WAITING) ? 1L : getQueuePosition(queueInfo);
    }

    /**
     * 주어진 토큰을 검증합니다.
     *
     * @param token 검증할 토큰
     * @throws CustomException 유효하지 않은 토큰인 경우
     */
    public void validateToken (UUID token) {
        // 토큰 조회 후 ACTIVE 상태인지 체크
        // 유효하지 않을 경우 예외반환
        QueueDomain queueInfo = getQueueInfoByToken(token);
        if (queueInfo.getStatus() != TokenStatus.ACTIVE) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }
    }

    /**
     * 주어진 토큰으로 QueueDomain 객체를 조회합니다.
     *
     * @param token 조회할 토큰
     * @return 토큰에 해당하는 QueueDomain 객체
     * @throws CustomException 토큰 정보가 존재하지 않는 경우
     */
    private QueueDomain getQueueInfoByToken(UUID token) {
        return queueRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND, ErrorCode.TOKEN_NOT_FOUND.getMessage()));
    }

    /**
     * 대기열 정보를 확인하여 초기 객체를 세팅합니다.
     *
     * @param userId 초기 객체 세팅할 사용자 ID
     * @return queueDomain 초기 객체
     */
    private QueueDomain createQueue(Long userId) {
        // 즉시입장 가능여부
        if (canEnterImmediately()) {
            // 즉시입장 가능 !! > 초기 활성화 세팅
            return QueueDomain.createActiveQueue(userId);
        }
        // 초기 비활성화 세팅
        return QueueDomain.createWaitingQueue(userId);
    }

    /**
     * 사용자가 즉시 입장 가능한지 확인합니다.
     *
     * @return 즉시 입장 가능 여부
     */
    private boolean canEnterImmediately() {
        // 활성화 인원이 서버 MAX 인원 안에 들어오는지 체크
        Long activeCount = queueRepository.countByStatus(TokenStatus.ACTIVE);
        return activeCount < QueueConstants.MAX_ACTIVE_USERS;
    }

    /**
     * 사용자의 대기순번을 계산합니다.
     *
     * @param currentQueueDomain 대기열 정보
     * @return 대기순번
     */
    private Long getQueuePosition(QueueDomain currentQueueDomain) {
        return calculateQueuePosition(currentQueueDomain, getLastActiveQueue());
    }

    /**
     * 가장 마지막에 입장한 대기열 정보 조회
     *
     * @return Optional<QueueDomain> 가장 마지막에 입장한 대기열 정보
     */
    private Optional<QueueDomain> getLastActiveQueue() {
        return queueRepository.getLastActiveQueue(TokenStatus.ACTIVE);
    }

    /**
     * 대기순번을 계산합니다.
     *
     * @param currentQueueDomain 현재 대기열 정보
     * @param lastActiveQueue 가장 마지막에 입장한 활성 대기열
     * @return 대기순번
     */
    private Long calculateQueuePosition(QueueDomain currentQueueDomain, Optional<QueueDomain> lastActiveQueue) {
        // 앞에 아무도 없는 경우 -> 1L
        // 내 대기순번 = 내 ID - 가장 마지막에 입장한 사람 ID
        return lastActiveQueue
                .map(queue -> currentQueueDomain.getQueueId() - queue.getQueueId())
                .orElse(1L);
    }

    /**
     * 결제 완료 시 대기열 토큰을 만료시킵니다.
     *
     * @param token 만료시킬 토큰
     * @return 만료된 토큰정보 result 객체
     * @throws CustomException 토큰 정보가 존재하지 않는 경우
     */
    @Transactional
    public QueueResult.expireTokenResult expireToken(UUID token) {

        // 만료 대상 토큰 조회
        QueueDomain expiredQueue = getQueueInfoByToken(token);
        // 만료 (EXPIRED) 상태로 갱신
        expiredQueue.updateQueueExpired();

        return QueueResult.expireTokenResult.from(queueRepository.save(expiredQueue));
    }

    /**
     * 대기열 상태를 업데이트합니다. (스케줄러 2분 주기 작업)
     */
    @Transactional
    public void updateQueueStatuses() {
        expireOldActiveTokens();
        activateWaitingTokens();
    }

    /**
     * 7분 이상 활성화된 토큰을 만료 상태로 변경합니다.
     */
    private void expireOldActiveTokens() {
        LocalDateTime sevenMinutesAgo = LocalDateTime.now().minusMinutes(QueueConstants.TOKEN_EXPIRATION_TIME_MINUTES);
        List<QueueDomain> queuesToExpire = queueRepository.findActiveTokensEnteredBefore(TokenStatus.ACTIVE, sevenMinutesAgo);

        if (queuesToExpire.isEmpty()) {
            log.info("만료 대상 토큰이 없습니다.");
            return;
        }

        // 만료상태로 세팅
        List<QueueDomain> expiredQueues = queuesToExpire.stream()
                .map(QueueDomain::updateQueueExpired)  // 레포지토리 믿으면 안됨. 여기 안에도 검증하는 로직이 있어야 함
                .collect(Collectors.toList());   // let?

        queueRepository.saveAll(expiredQueues);
        log.info("총 {}개의 토큰이 만료되었습니다.", expiredQueues.size());
    }

    /**
     * 대기 중인 토큰을 활성화 상태로 변경합니다.
     * 최대 활성화 가능한 사용자 수 - 현재 활성화된 토큰 수
     */
    private void activateWaitingTokens() {
        // 빈 사이즈 만큼 활성화 (대기중 > 활성화)
        // 결제완료 후 토큰 만료할 수도 있으므로 > [만료시킨 토큰 리스트 != 활성화 가능한 개수] 일 수 있음 !
        // > 활성화 상태 개수 새로 체크
        Long currentActiveQueueCount = queueRepository.countByStatus(TokenStatus.ACTIVE);
        int maxActiveUsers = QueueConstants.MAX_ACTIVE_USERS;
        int slotsAvailableForActivation = (int) (maxActiveUsers - currentActiveQueueCount);

        if (slotsAvailableForActivation <= 0) {
            log.info("활성화 가능한 슬롯이 없습니다.");
            return;
        }

        Pageable pageable = PageRequest.of(0, slotsAvailableForActivation);
        List<QueueDomain> queuesToActivate = queueRepository.findByStatusOrderByCreatedAtAsc(TokenStatus.WAITING, pageable);

        if (queuesToActivate.isEmpty()) {
            log.info("대기 중인 토큰이 없습니다.");
            return;
        }

        // 활성화 상태로 세팅
        List<QueueDomain> activeQueues = queuesToActivate.stream()
                .map(QueueDomain::updateQueueActive)
                .collect(Collectors.toList());

        queueRepository.saveAll(activeQueues);
        log.info("총 {}개의 대기 중인 토큰이 활성화되었습니다.", activeQueues.size());
    }
}
