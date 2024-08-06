package org.hhplus.ticketing.interfaces.scheduler;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.application.queue.QueueFacade;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueScheduler {

    private final QueueFacade queueFacade;

    /**
     * 대기열 상태를 업데이트합니다.
     * 1. 활성토큰 추가
     * 2. 대기열에서 제거
     */
    @Scheduled(fixedRate = QueueConstants.INTERVAL_SECONDS * 1000) // 10초 간격으로 실행
    public void activate() {
        queueFacade.activate();
    }
}
