package org.hhplus.ticketing.domain.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class QueueResult {

    // 토큰 발급 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IssueTokenResult {
        private Long userId;                    // 유저ID
        private UUID token;                     // 토큰

        public static IssueTokenResult from(Queue domain) {
            return IssueTokenResult.builder()
                    .userId(domain.getUserId())
                    .token(domain.getToken())
                    .build();
        }
    }

    // 토큰 상태 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class QueueStatusResult {
        private Long userId;                    // 유저ID
        private UUID token;                     // 토큰
        private Long position;                  // 대기순서
        private Queue.Status status;             // 토큰상태

        public static QueueStatusResult from(Queue domain) {
            return QueueStatusResult.builder()
                    .userId(domain.getUserId())
                    .token(domain.getToken())
                    .status(domain.getStatus())
                    .build();
        }
    }

    // 토큰 만료 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExpireTokenResult {
        private Long userId;                    // 유저ID

        public static ExpireTokenResult from(Queue domain) {
            return ExpireTokenResult.builder()
                    .userId(domain.getUserId())
                    .build();
        }
    }
}
