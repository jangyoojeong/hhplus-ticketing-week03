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
    public static class UserInfoResult {
        private Long userId;           // 유저ID (키값)
        private String userName;       // 유저 이름

        public static UserInfoResult from(UserInfoDomain domain) {
            return UserInfoResult.builder()
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
    public static class AddPointResult {

        private Long userId;                    // userId
        private int point;                      // 충전 후 포인트 잔액

        public static AddPointResult from(UserPointDomain domain) {
            return AddPointResult.builder()
                    .userId(domain.getUserId())
                    .point(domain.getPoint())
                    .build();
        }
    }

    // 사용자 잔액 차감 result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsePointResult {

        private Long userId;                    // userId
        private int point;                      // 차감 후 포인트 잔액

        public static UsePointResult from(UserPointDomain domain) {
            return UsePointResult.builder()
                    .userId(domain.getUserId())
                    .point(domain.getPoint())
                    .build();
        }
    }

    // 사용자 잔액 조회 result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserPointResult {

        private Long userId;                    // userId
        private int point;                      // 포인트 잔액

        public static UserPointResult from(UserPointDomain domain) {
            return UserPointResult.builder()
                    .userId(domain.getUserId())
                    .point(domain.getPoint())
                    .build();
        }
    }
}
