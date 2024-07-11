package org.hhplus.ticketing.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.user.model.enums.PointType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPointHistoryDomain {
    private Long userPointHistoryId; // 포인트이력ID (키값)
    private Long userId;             // 유저ID
    private int amount;              // 변경금액
    private PointType type;          // 유형(CHARGE/USE)

    public UserPointHistoryDomain(Long userId, int amount, PointType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
    }
}