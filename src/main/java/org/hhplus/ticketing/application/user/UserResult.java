package org.hhplus.ticketing.application.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.user.model.UserPoint;

public class UserResult {

    // 사용자 잔액 충전 result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChargePoint {

        private Long userId;                    // userId
        private int point;                      // 충전 후 포인트 잔액

        public static ChargePoint from(UserPoint domain) {
            return ChargePoint.builder()
                    .userId(domain.getUserId())
                    .point(domain.getPoint())
                    .build();
        }
    }

    // 사용자 차감 후 잔액 result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsePoint {

        private Long userId;                    // userId
        private int point;                      // 차감 후 포인트 잔액

        public static UsePoint from(UserPoint domain) {
            return UsePoint.builder()
                    .userId(domain.getUserId())
                    .point(domain.getPoint())
                    .build();
        }
    }

    // 사용자 잔액 result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetPoint {

        private Long userId;                    // userId
        private int point;                      // 차감 후 포인트 잔액

        public static GetPoint from(UserPoint domain) {
            return GetPoint.builder()
                    .userId(domain.getUserId())
                    .point(domain.getPoint())
                    .build();
        }
    }
}
