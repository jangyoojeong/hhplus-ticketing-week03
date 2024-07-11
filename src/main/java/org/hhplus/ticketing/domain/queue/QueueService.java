package org.hhplus.ticketing.domain.queue;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;
import org.hhplus.ticketing.domain.queue.model.QueueDomain;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.queue.model.enums.TokenStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Service
@RequiredArgsConstructor
public class QueueService {

    private static final Logger log = LoggerFactory.getLogger(QueueService.class);

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
     */
    public QueueResult.QueueStatusResult getQueueStatus(UUID token) {

        Long queuePosition;

        QueueDomain queueInfo = queueRepository.findByToken(token).orElseThrow(()
                -> new IllegalArgumentException("토큰 정보가 존재하지 않습니다."));

        // 상태 != WAITING -> 대기순번 0 RETURN
        // 스케줄러를 통해 상태값이 변경된 경우 내 차례 확인 !!
        if (queueInfo.getStatus() != TokenStatus.WAITING) {
            queuePosition = 1L;
        }
        // 상태 = WAITING
        else {
            // 내 대기순번
            queuePosition = getQueuePosition(queueInfo);
        }
        
        return new QueueResult.QueueStatusResult(queueInfo.getUserId(), queueInfo.getToken(), queuePosition);
    }

    /**
     * 주어진 토큰을 검증합니다.
     *
     * @param token 검증할 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken (UUID token) {

        // 토큰 조회 후 ACTIVE 상태인지 체크
        // true : 유효 / false : 유효하지 않음
        QueueDomain queueInfo = queueRepository.findByToken(token).orElseThrow(()
                -> new IllegalArgumentException("토큰 정보가 존재하지 않습니다."));
        
        if (queueInfo.getStatus() == TokenStatus.ACTIVE) {
            return true;
        }
        return false;
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
            // 증시입장 가능 !! > 초기 활성화 세팅
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
        // 현재 대기중인 인원 체크
        Long waitingCount = queueRepository.countByStatus(TokenStatus.WAITING);
        if (waitingCount == 0) {
            // 대기중인 인원이 없는데, 활성화 인원이 서버 MAX 인원 안에 들어오는지 체크
            Long activeCount = queueRepository.countByStatus(TokenStatus.ACTIVE);
            return activeCount < QueueDomain.getMaxActiveUsers();
        }
        return false;
    }
    
    /**
     * 사용자의 대기순번을 계산합니다.
     *
     * @param currentQueueDomain 대기열 정보
     * @return 대기순번
     */
    private Long getQueuePosition(QueueDomain currentQueueDomain) {
        // 가장 마지막에 입장한 사람 순번 조회
        Optional<QueueDomain> lastActiveQueue = queueRepository.getLastActiveQueue(TokenStatus.ACTIVE);

        QueueDomain lastQueue = lastActiveQueue.orElse(null);

        // 앞에 아무도 없는 경우
        if (lastQueue == null) {
            return 1L;
        }
        else {
            // 내 대기순번 = 내 ID - 가장 마지막에 입장한 사람 ID
            return currentQueueDomain.getQueueId() - lastQueue.getQueueId();
        }
    }

    /**
     * 결제 완료 시 대기열 토큰을 만료시킵니다.
     *
     * @param token 만료시킬 토큰
     * @return 만료된 토큰정보 result 객체
     */
    @Transactional
    public QueueResult.expireTokenResult expireToken(UUID token) {

        // 만료 대상 토큰 조회
        QueueDomain expiredQueue = queueRepository.findByToken(token).orElseThrow(()
                -> new IllegalArgumentException("토큰 정보가 존재하지 않습니다."));
    
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
        LocalDateTime sevenMinutesAgo = LocalDateTime.now().minusMinutes(7);
        List<QueueDomain> queuesToExpire = queueRepository.findActiveTokensEnteredBefore(TokenStatus.ACTIVE, sevenMinutesAgo);

        // 만료상태로 세팅
        List<QueueDomain> expiredQueues = queuesToExpire.stream()
                .map(QueueDomain::updateQueueExpired)
                .collect(Collectors.toList());

        queueRepository.saveAll(expiredQueues);
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
        int maxActiveUsers = QueueDomain.getMaxActiveUsers();
        int slotsAvailableForActivation = (int) (maxActiveUsers - currentActiveQueueCount);

        if (slotsAvailableForActivation > 0) {
            Pageable pageable = PageRequest.of(0, slotsAvailableForActivation);
            List<QueueDomain> queuesToActivate = queueRepository.findByStatusOrderByCreatedAtAsc(TokenStatus.WAITING, pageable);

            // 활성화 상태로 세팅
            List<QueueDomain> activeQueues = queuesToActivate.stream()
                    .map(QueueDomain::updateQueueActive)
                    .collect(Collectors.toList());

            queueRepository.saveAll(activeQueues);
        }
    }
}
