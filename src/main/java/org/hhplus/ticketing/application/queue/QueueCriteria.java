package org.hhplus.ticketing.application.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;

public class QueueCriteria {

    // 토큰 발급 요청 criteria
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IssueToken {
        private Long userId;                    // 유저ID

        public QueueCommand.IssueToken toCommand() {
            return QueueCommand.IssueToken
                    .builder()
                    .userId(this.getUserId())
                    .build();
        }
    }
}
