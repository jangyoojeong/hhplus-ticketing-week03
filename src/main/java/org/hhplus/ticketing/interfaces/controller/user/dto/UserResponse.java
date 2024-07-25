package org.hhplus.ticketing.interfaces.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.user.model.UserResult;

public class UserResponse {

    // 사용자 잔액 충전 response
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChargePointResponse {

        private Long userId;                    // userId
        private int point;                      // 충전 후 포인트 잔액

        public static ChargePointResponse from(UserResult.ChargePointResult result) {
            return ChargePointResponse.builder()
                    .userId(result.getUserId())
                    .point(result.getPoint())
                    .build();
        }
    }

    // 사용자 잔액 조회 response
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserPointResponse {

        private Long userId;                    // userId
        private int point;                      // 포인트 잔액

        public static UserPointResponse from(UserResult.UserPointResult result) {
            return UserPointResponse.builder()
                    .userId(result.getUserId())
                    .point(result.getPoint())
                    .build();
        }
    }


}
