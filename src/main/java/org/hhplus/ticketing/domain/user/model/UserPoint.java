package org.hhplus.ticketing.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;

/**
 * 유저 포인트 도메인 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPoint {
    private Long userPointId;      // 포인트ID (키값)
    private Long userId;           // 유저ID
    private int point;             // 포인트
    private Long version = 0L;     // 낙관적 락 버전 필드

    public UserPoint chargePoint(int amount) {
        if (amount <= 0) {
            throw new CustomException(ErrorCode.INVALID_AMOUNT_VALUE);
        }
        this.point += amount;
        return this;
    }

    public UserPoint usePoint(int amount) {
        if (amount <= 0) {
            throw new CustomException(ErrorCode.INVALID_AMOUNT_VALUE);
        }
        if (this.point < amount) {
            throw new CustomException(ErrorCode.INSUFFICIENT_POINTS);
        }
        this.point -= amount;
        return this;
    }
}