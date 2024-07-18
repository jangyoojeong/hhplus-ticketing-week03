package org.hhplus.ticketing.interfaces.scheduler;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.application.concert.facade.ConcertFacade;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertScheduler {

    private final ConcertFacade concertFacade;

    /**
     * 2분 주기로 좌석 임시예약이 만료된 것을 처리합니다.
     * 1. 좌석 (예약됨 > 사용가능)
     */
    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void releaseTemporaryReservations() {
        concertFacade.releaseTemporaryReservations();
    }
}
