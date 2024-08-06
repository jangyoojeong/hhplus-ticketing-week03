package org.hhplus.ticketing.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserResult {

    // 사용자 조회 result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetUser {
        private Long userId;           // 유저ID (키값)
        private String userName;       // 유저 이름

        public static GetUser from(UserInfo domain) {
            return GetUser.builder()
                    .userId(domain.getUserId())
                    .userName(domain.getUserName())
                    .build();
        }
    }

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
