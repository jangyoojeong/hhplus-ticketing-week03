package org.hhplus.ticketing.interfaces.scheduler;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.outbox.OutboxService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxService outboxService;

    // 5초마다 메시지 재발행 시도
    @Scheduled(fixedRate = 5000)
    public void retryFailedMessages() {
        outboxService.retryFailedMessages();
    }
}
