package org.hhplus.ticketing.domain.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    @Transactional(rollbackFor = {Exception.class})
    public QueueResult.IssueTokenResult issueToken(QueueCommand.IssueTokenCommand command) {
        Queue queue = createQueue(command.getUserId());
        return QueueResult.IssueTokenResult.from(queueRepository.save(queue));
    }

    /**
     * 대기열 정보를 확인하여 초기 객체를 세팅합니다.
     *
     * @param userId 초기 객체 세팅할 사용자 ID
     * @return queue 초기 객체
     */
    private Queue createQueue(Long userId) {
        Long activeCount = queueRepository.countByStatus(Queue.Status.ACTIVE);
        return activeCount < QueueConstants.MAX_ACTIVE_USERS ? Queue.createActive(userId) : Queue.createWaiting(userId);
    }

    /**
     * 사용자의 대기열 상태(대기순번 등)를 반환합니다.
     *
     * @param token 대기열 상태 조회할 token
     * @return 대기순번 등 대기열 상태 result 객체
     * @throws CustomException 토큰 정보가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public QueueResult.QueueStatusResult getQueueStatus(UUID token) {
        Queue queue = getQueue(token);
        Long position = queue.getStatus() != Queue.Status.WAITING ? 0L : queue.getQueuePosition(getLastActiveQueue());
        return QueueResult.QueueStatusResult.builder()
                .userId(queue.getUserId())
                .token(queue.getToken())
                .position(position)
                .status(queue.getStatus())
                .build();
    }

    /**
     * 주어진 토큰을 검증합니다.
     *
     * @param token 검증할 토큰
     * @throws CustomException 유효하지 않은 토큰인 경우
     */
    public void validateToken (UUID token) {
        Queue queue = getQueue(token);
        queue.validateActiveStatus();
    }

    /**
     * 주어진 토큰으로 queue 객체를 조회합니다.
     *
     * @param token 조회할 토큰
     * @return 토큰에 해당하는 queue 객체
     * @throws CustomException 토큰 정보가 존재하지 않는 경우
     */
    private Queue getQueue(UUID token) {
        return queueRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));
    }

    /**
     * 가장 마지막에 입장한 대기열 정보 조회
     *
     * @return Optional<queue> 가장 마지막에 입장한 대기열 정보
     */
    private Optional<Queue> getLastActiveQueue() {
        return queueRepository.getLastActiveQueue(Queue.Status.ACTIVE);
    }

    /**
     * 결제 완료 시 대기열 토큰을 만료시킵니다.
     *
     * @param token 만료시킬 토큰
     * @return 만료된 토큰정보 result 객체
     * @throws CustomException 토큰 정보가 존재하지 않는 경우
     */
    @Transactional(rollbackFor = {Exception.class})
    public QueueResult.ExpireTokenResult expireToken(UUID token) {
        Queue queue = getQueue(token);
        queue.setExpired();
        return QueueResult.ExpireTokenResult.from(queueRepository.save(queue));
    }

    /**
     * 대기열 상태를 업데이트합니다. (스케줄러 2분 주기 작업)
     */
    @Transactional(rollbackFor = {Exception.class})
    public void refreshQueue() {
        expire();
        activate();
    }

    /**
     * 토큰을 만료 상태로 변경합니다.
     */
    private void expire() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(QueueConstants.TOKEN_EXPIRATION_MINUTES);
        List<Queue> expiredTokens = queueRepository.getExpiredTokens(Queue.Status.ACTIVE, expirationTime);
        expiredTokens.forEach(Queue::setExpired);
        queueRepository.saveAll(expiredTokens);
        log.info("총 {}개의 토큰이 만료되었습니다.", expiredTokens.size());
    }

    /**
     * 대기 중인 토큰을 활성화 상태로 변경합니다.
     * 최대 활성화 가능한 사용자 수 - 현재 활성화된 토큰 수
     */
    private void activate() {
        Long activeCount  = queueRepository.countByStatus(Queue.Status.ACTIVE);
        int slotsAvailable = (int) (QueueConstants.MAX_ACTIVE_USERS - activeCount);

        if (slotsAvailable > 0) {
            Pageable pageable = PageRequest.of(0, slotsAvailable);
            List<Queue> activeQueues = queueRepository.getActivatableTokens(Queue.Status.WAITING, pageable);
            activeQueues.forEach(Queue::setActive);
            queueRepository.saveAll(activeQueues);
            log.info("총 {}개의 대기 중인 토큰이 활성화되었습니다.", activeQueues.size());
        }
    }
}
