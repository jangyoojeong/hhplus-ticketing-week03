package org.hhplus.ticketing.domain.queue.model;

import lombok.*;

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

        public static IssueTokenResult from(QueueDomain domain) {
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
    @Builder
    public static class QueueStatusResult {
        private Long userId;                    // 유저ID
        private UUID token;                     // 토큰
        private Long queuePosition;             // 대기순서

        public static QueueStatusResult from(QueueDomain domain) {
            return QueueStatusResult.builder()
                    .userId(domain.getUserId())
                    .token(domain.getToken())
                    .build();
        }
    }

    // 토큰 만료 Result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class expireTokenResult {
        private Long userId;                    // 유저ID

        public static expireTokenResult from(QueueDomain domain) {
            return expireTokenResult.builder()
                    .userId(domain.getUserId())
                    .build();
        }
    }
}
