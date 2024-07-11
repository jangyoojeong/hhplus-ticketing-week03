package org.hhplus.ticketing.domain.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class QueueCommand {

    // 토큰 발급 요청 command
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IssueTokenCommand {
        private Long userId;                    // 유저ID
    }
}
