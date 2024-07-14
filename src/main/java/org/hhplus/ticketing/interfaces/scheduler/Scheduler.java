package org.hhplus.ticketing.interfaces.scheduler;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.application.concert.facade.ConcertFacade;
import org.hhplus.ticketing.application.queue.facade.QueueFacade;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final QueueFacade queueFacade;
    private final ConcertFacade concertFacade;

    /**
     * 대기열 상태를 업데이트합니다.
     * 1. 만료 대상 토큰 만료
     * 2. 빈자리 만큼 활성화
     * >> 스케줄러 2분 주기 작업 (@Scheduled(fixedRate = 120000))
     */
    @Scheduled(fixedRate = 120000)
    public void updateQueueStatuses() {
        queueFacade.updateQueueStatuses();
    }

    /**
     * 2분 주기로 좌석 임시예약이 만료된 것을 처리합니다.
     * 1. 좌석 (예약됨 > 사용가능)
     */
    @Scheduled(fixedRate = 120000)
    public void releaseTemporaryReservations() {
        concertFacade.releaseTemporaryReservations();
    }
}
