package org.hhplus.ticketing.interfaces.controller.queue.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;

public class QueueRequest {

    // 토큰 발급 request
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IssueTokenRequest {

        @NotNull(message = "사용자 ID는 비어 있을 수 없습니다.")
        private Long userId;                    // 유저ID

        public QueueCommand.IssueTokenCommand toCommand() {
            return QueueCommand.IssueTokenCommand.builder().userId(this.getUserId()).build();
        }
    }
}