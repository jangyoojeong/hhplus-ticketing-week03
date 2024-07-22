package org.hhplus.ticketing.interfaces.scheduler;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.application.queue.facade.QueueFacade;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueScheduler {

    private final QueueFacade queueFacade;

    /**
     * 대기열 상태를 업데이트합니다.
     * 1. 만료 대상 토큰 만료
     * 2. 빈자리 만큼 활성화
     */
    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void updateQueueStatuses() {
        queueFacade.updateQueueStatuses();
    }

}
