package org.hhplus.ticketing.interfaces.controller.queue.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.domain.queue.model.QueueResult;

public class QueueResponse {

    // 발급된 토큰과 대기열 정보를 포함한 response
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IssueTokenResponse {

        private Long userId;                    // 유저ID

        public static IssueTokenResponse from(QueueResult.IssueTokenResult result) {
            return IssueTokenResponse.builder()
                    .userId(result.getUserId())
                    .build();
        }
    }

    // 대기순번 등 대기열 상태 Response
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QueueStatusResponse {

        private Long userId;                    // 유저ID
        private Long position;                  // 대기순서
        private Queue.Status status;             // 토큰상태

        public static QueueStatusResponse from(QueueResult.QueueStatusResult result) {
            return QueueStatusResponse.builder()
                    .userId(result.getUserId())
                    .position(result.getPosition())
                    .status(result.getStatus())
                    .build();
        }
    }
}
