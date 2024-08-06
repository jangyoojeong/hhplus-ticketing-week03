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
    public static class ChargePoint {

        private Long userId;                    // userId
        private int point;                      // 충전 후 포인트 잔액

        public static ChargePoint from(UserResult.ChargePoint result) {
            return ChargePoint.builder()
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
    public static class UserPoint {

        private Long userId;                    // userId
        private int point;                      // 포인트 잔액

        public static UserPoint from(UserResult.GetPoint result) {
            return UserPoint.builder()
                    .userId(result.getUserId())
                    .point(result.getPoint())
                    .build();
        }
    }


}
