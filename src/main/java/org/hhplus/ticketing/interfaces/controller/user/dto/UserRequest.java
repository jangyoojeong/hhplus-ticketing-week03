package org.hhplus.ticketing.interfaces.controller.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.user.model.UserCommand;

public class UserRequest {

    // 사용자 잔액 충전 request
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddPointRequest {

        @NotNull(message = "사용자 ID는 비어 있을 수 없습니다.")
        private Long userId;                    // 유저ID
        @Min(value = 1, message = "충전 금액은 1원 이상이어야 합니다.")
        private int amount;                     // 충전금액

        public UserCommand.AddPointCommand toCommand() {
            return UserCommand.AddPointCommand.builder().userId(this.getUserId()).amount(this.getAmount()).build();
        }
    }


}
