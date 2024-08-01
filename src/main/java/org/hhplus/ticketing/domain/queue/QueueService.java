package org.hhplus.ticketing.domain.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;
import org.springframework.stereotype.Service;

import java.util.Set;

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
     * @return 발급된 토큰과 대기열 정보를 포함한 result 객체
     */
    public QueueResult.IssueToken issueToken() {
        Queue queue = Queue.create();
        Long activeCount = queueRepository.countActiveTokens();

        if (activeCount < QueueConstants.MAX_ACTIVE_TOKENS) {
            queueRepository.addActive(queue);
            return new QueueResult.IssueToken(queue.getToken(), 0L, "");
        }

        queueRepository.addWaiting(queue);
        Long position = getWaitingPosition(queue.getToken());
        return new QueueResult.IssueToken(queue.getToken(), position, Queue.getRemainingWaitTime(position));
    }

    /**
     * 주어진 토큰의 대기열 내 순위를 반환합니다.
     *
     * @param token 대기열 순위를 조회할 토큰
     * @return 대기열 내 순위
     * @throws CustomException 토큰 정보가 존재하지 않는 경우 발생하는 예외
     */
    public Long getWaitingPosition(String token) {
        Long position = queueRepository.getWaitingPosition(token);
        return Queue.getPosition(position);
    }

    /**
     * 사용자의 대기열 상태(대기순번 등)를 반환합니다.
     *
     * @param token 대기열 상태 조회할 토큰
     * @return 대기순번 등 대기열 상태 result 객체
     * @throws CustomException 토큰 정보가 존재하지 않는 경우 발생하는 예외
     */
    public QueueResult.QueueStatus getQueueStatus(String token) {
        Long position = getWaitingPosition(token);
        return new QueueResult.QueueStatus(position, Queue.getRemainingWaitTime(position));
    }

    /**
     * 주어진 토큰을 검증합니다.
     *
     * @param token 검증할 토큰
     * @throws CustomException 유효하지 않은 토큰인 경우
     */
    public void validateToken (String token) {
        if (!queueRepository.isValid(token)) throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    /**
     * 결제 완료 시 대기열 토큰을 만료시킵니다.
     *
     * @param token 만료시킬 토큰
     */
    public void expireToken(String token) {
        if (!queueRepository.isValid(token)) throw new CustomException(ErrorCode.INVALID_TOKEN);
        queueRepository.delActive(token);
    }

    /**
     * 대기 중인 토큰을 활성화 상태로 변경합니다.
     * 주기적으로 실행되어 일정 수의 대기 중인 토큰을 활성화 상태로 변경합니다.
     */
    public void activate() {
        Set<String> waitingTokens = queueRepository.getActivatableTokens(0, QueueConstants.MAX_ACTIVE_TOKENS - 1);
        if (waitingTokens.isEmpty()) return;

        queueRepository.activate(waitingTokens);
        log.info("총 {}개의 대기 중인 토큰이 활성화되었습니다.", waitingTokens.size());
    }
}
