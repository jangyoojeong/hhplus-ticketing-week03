package org.hhplus.ticketing.application.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.user.model.UserCommand;

public class UserCriteria {

    // 사용자 잔액 충전 criteria
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChargePoint {
        private Long userId;                    // userId
        private int amount;                     // 충전금액

        public UserCommand.ChargePoint toCommand() {
            return UserCommand.ChargePoint
                    .builder()
                    .userId(this.getUserId())
                    .amount(this.getAmount())
                    .build();
        }
    }

    // 사용자 잔액 차감 criteria
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsePoint {
        private Long userId;                    // userId
        private int amount;                     // 차감금액
    }
}
