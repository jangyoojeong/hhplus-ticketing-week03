package org.hhplus.ticketing.domain.user.model;

import lombok.*;

public class UserCommand {

    // 사용자 잔액 충전 command
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddPointCommand {
        private Long userId;                    // userId
        private int amount;                     // 충전금액
    }

    // 사용자 잔액 차감 command
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsePointCommand {
        private Long userId;                    // userId
        private int amount;                     // 차감금액
    }
}
