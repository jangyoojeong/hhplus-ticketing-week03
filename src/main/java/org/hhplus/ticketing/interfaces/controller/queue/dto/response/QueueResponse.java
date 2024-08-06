package org.hhplus.ticketing.interfaces.controller.queue.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.domain.queue.model.QueueResult;

public class QueueResponse {

    // 대기순번 등 대기열 상태 Response
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QueueStatus {

        private Long position;                    // 대기순서
        private String remainingTime;             // 잔여시간

        public static QueueStatus from(QueueResult.QueueStatus result) {
            return QueueStatus.builder()
                    .position(result.getPosition())
                    .remainingTime(result.getRemainingTime())
                    .build();
        }
    }
}
