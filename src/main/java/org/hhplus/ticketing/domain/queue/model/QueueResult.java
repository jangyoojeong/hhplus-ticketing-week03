package org.hhplus.ticketing.domain.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class QueueResult {

    // 토큰 발급 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IssueToken {
        private String token;                     // 토큰
        private Long position;                    // 대기순서
        private String remainingTime;             // 잔여시간
        private Queue.Status status;              // 상태

        public static QueueResult.IssueToken from(Queue domain) {
            return QueueResult.IssueToken.builder()
                    .token(domain.getToken())
                    .position(domain.getPosition())
                    .remainingTime(domain.getRemainingTime())
                    .status(domain.getStatus())
                    .build();
        }
    }

    // 토큰 상태 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class QueueStatus {
        private Long position;                    // 대기순서
        private String remainingTime;             // 잔여시간
        private Queue.Status status;              // 상태

        public static QueueResult.QueueStatus from(Queue domain) {
            return QueueResult.QueueStatus.builder()
                    .position(domain.getPosition())
                    .remainingTime(domain.getRemainingTime())
                    .status(domain.getStatus())
                    .build();
        }
    }

}
