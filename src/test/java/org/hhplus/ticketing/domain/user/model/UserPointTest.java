package org.hhplus.ticketing.domain.user.model;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserPointTest {

    @Test
    @DisplayName("🟢 [포인트_충전_테스트]")
    void chargePointTest_100포인트에_50포인트_충전시_150포인트가_리턴된다() {
        Long userId = 1L;
        int initialPoint = 100;
        int chargeAmount = 50;

        UserPoint userPoint = UserPoint.builder()
                .userId(userId)
                .point(initialPoint)
                .build();
        userPoint.chargePoint(chargeAmount);

        assertThat(userPoint.getPoint()).isEqualTo(initialPoint + chargeAmount);
    }

    @Test
    @DisplayName("🔴 [포인트_충전_테스트]")
    void chargePointTest_포인트가_유효하지_않으면_INVALID_AMOUNT_VALUE_예외반환() {
        Long userId = 1L;
        int initialPoint = 50;
        int chargeAmount = 0;

        UserPoint userPoint = UserPoint.builder()
                .userId(userId)
                .point(initialPoint)
                .build();

        assertThatThrownBy(() -> userPoint.usePoint(chargeAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AMOUNT_VALUE.getMessage());
    }

    @Test
    @DisplayName("🟢 [포인트_사용_테스트]")
    void usePointTest_100포인트에_50포인트_사용시_50포인트가_리턴된다() {
        Long userId = 1L;
        int initialPoint = 100;
        int useAmount = 50;

        UserPoint userPoint = UserPoint.builder()
                .userId(userId)
                .point(initialPoint)
                .build();
        userPoint.usePoint(useAmount);

        assertThat(userPoint.getPoint()).isEqualTo(initialPoint - useAmount);
    }

    @Test
    @DisplayName("🔴 [포인트_사용_테스트]")
    void usePointTest_포인트가_부족하면_INSUFFICIENT_POINTS_예외반환() {
        Long userId = 1L;
        int initialPoint = 50;
        int useAmount = 100;

        UserPoint userPoint = UserPoint.builder()
                .userId(userId)
                .point(initialPoint)
                .build();

        assertThatThrownBy(() -> userPoint.usePoint(useAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_POINTS.getMessage());
    }

    @Test
    @DisplayName("🔴 [포인트_사용_테스트]")
    void usePointTest_포인트가_유효하지_않으면_INVALID_AMOUNT_VALUE_예외반환() {
        Long userId = 1L;
        int initialPoint = 50;
        int useAmount = 0;

        UserPoint userPoint = UserPoint.builder()
                .userId(userId)
                .point(initialPoint)
                .build();

        assertThatThrownBy(() -> userPoint.usePoint(useAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AMOUNT_VALUE.getMessage());
    }
}