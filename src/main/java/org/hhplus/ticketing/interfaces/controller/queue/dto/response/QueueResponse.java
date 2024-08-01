package org.hhplus.ticketing.interfaces.controller.queue.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.queue.model.QueueResult;

public class QueueResponse {

    // 발급된 토큰과 대기열 정보를 포함한 response
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IssueTokenResponse {

        private Long position;                    // 대기순서
        private String remainingTime;             // 잔여시간

        public static IssueTokenResponse from(QueueResult.IssueToken result) {
            return IssueTokenResponse.builder()
                    .position(result.getPosition())
                    .remainingTime(result.getRemainingTime())
                    .build();
        }
    }

    // 대기순번 등 대기열 상태 Response
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QueueStatusResponse {

        private Long position;                    // 대기순서
        private String remainingTime;             // 잔여시간

        public static QueueStatusResponse from(QueueResult.QueueStatus result) {
            return QueueStatusResponse.builder()
                    .position(result.getPosition())
                    .remainingTime(result.getRemainingTime())
                    .build();
        }
    }
}
