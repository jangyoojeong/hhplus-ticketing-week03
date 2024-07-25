package org.hhplus.ticketing.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPointHistory {
    private Long userPointHistoryId; // 포인트이력ID (키값)
    private Long userId;             // 유저ID
    private int amount;              // 변경금액
    private Type type;               // 유형(CHARGE/USE)

    public static UserPointHistory create(Long userId, int amount, UserPointHistory.Type type) {
        return UserPointHistory.builder()
                .userId(userId)
                .amount(amount)
                .type(type)
                .build();
    }

    public enum Type {
        CHARGE,     // 포인트 충전
        USE         // 포인트 사용
    }
}