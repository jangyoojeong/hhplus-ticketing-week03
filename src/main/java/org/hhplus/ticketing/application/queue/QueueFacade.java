package org.hhplus.ticketing.application.queue;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.queue.QueueService;
import org.hhplus.ticketing.domain.user.UserInfoService;
import org.springframework.stereotype.Component;

/**
 * 대기열 관련 비즈니스 로직을 캡슐화하는 파사드 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class QueueFacade {

    private final QueueService queueService;
    private final UserInfoService userInfoService;

    /**
     * 콘서트 대기열에 입장할 때 사용하는 토큰을 발급합니다.
     * 사용자는 이 토큰을 통해 대기열에 대한 인증을 받을 수 있습니다.
     *
     * @param command 토큰 발급 요청 command 객체
     * @return 발급된 토큰과 대기열 정보를 포함한 result 객체
     */
    public QueueResult.IssueToken issueToken(QueueCriteria.IssueToken command) {

        // 1. 유저 정보 확인 (유저 정보 없을 시 예외 리턴)
        userInfoService.validateUser(command.getUserId());

        // 2. 대기열 토큰 발급 및 리턴
        return QueueResult.IssueToken.from(queueService.issueToken());
    }

    /**
     * 사용자의 대기열 상태(대기순번 등)를 반환합니다.
     *
     * @param token 대기열 상태 조회할 token
     * @return 대기순번 등 대기열 상태 result 객체
     */
    public QueueResult.QueueStatus getQueueStatus(String token) {
        return QueueResult.QueueStatus.from(queueService.getQueueStatus(token));
    }

    /**
     * 주어진 토큰을 검증합니다.
     *
     * @param token 검증할 토큰
     */
    public void validateToken (String token) {
        queueService.validateToken(token);
    }

    /**
     * 대기열 상태를 업데이트합니다.
     * 1. 만료 대상 토큰 만료
     * 2. 빈자리 만큼 활성화
     */
    public void activate() {
        queueService.activate();
    }
}
