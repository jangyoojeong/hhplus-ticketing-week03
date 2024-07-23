package org.hhplus.ticketing.domain.user.model;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserPointTest {

    @Test
    @DisplayName("🟢 유저포인트_객체_생성_테스트_유저ID와_포인트로_객체가_생성된다")
    void createPointTest_유저포인트_객체_생성_테스트_유저ID와_포인트로_객체가_생성된다() {
        Long userId = 1L;
        int point = 100;

        UserPoint userPoint = UserPoint.create(userId, point);

        assertThat(userPoint.getUserId()).isEqualTo(userId);
        assertThat(userPoint.getPoint()).isEqualTo(point);
    }

    @Test
    @DisplayName("🟢 디폴트_유저포인트_객체_생성_테스트_포인트가_0인_객체가_생성된다")
    void creatTest_디폴트_유저포인트_객체_생성_테스트_포인트가_0인_객체가_생성된다() {
        Long userId = 1L;

        UserPoint userPoint = UserPoint.create(userId);

        assertThat(userPoint.getUserId()).isEqualTo(userId);
        assertThat(userPoint.getPoint()).isEqualTo(0);
    }

    @Test
    @DisplayName("🟢 포인트_충전_테스트_100포인트에_50포인트_충전시_150포인트가_리턴된다")
    void chargePointTest_포인트_충전_테스트_100포인트에_50포인트_충전시_150포인트가_리턴된다() {
        Long userId = 1L;
        int initialPoint = 100;
        int chargeAmount = 50;

        UserPoint userPoint = UserPoint.create(userId, initialPoint);
        userPoint.chargePoint(chargeAmount);

        assertThat(userPoint.getPoint()).isEqualTo(initialPoint + chargeAmount);
    }

    @Test
    @DisplayName("🔴 포인트_충전_테스트_포인트가_유효하지_않으면_INVALID_AMOUNT_VALUE_예외반환")
    void chargePointTest_포인트_충전_테스트_포인트가_유효하지_않으면_INVALID_AMOUNT_VALUE_예외반환() {
        Long userId = 1L;
        int initialPoint = 50;
        int chargeAmount = 0;

        UserPoint userPoint = UserPoint.create(userId, initialPoint);

        assertThatThrownBy(() -> userPoint.usePoint(chargeAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AMOUNT_VALUE.getMessage());
    }

    @Test
    @DisplayName("🟢 포인트_사용_테스트_100포인트에_50포인트_사용시_50포인트가_리턴된다")
    void usePointTest_포인트_사용_테스트_100포인트에_50포인트_사용시_50포인트가_리턴된다() {
        Long userId = 1L;
        int initialPoint = 100;
        int useAmount = 50;

        UserPoint userPoint = UserPoint.create(userId, initialPoint);
        userPoint.usePoint(useAmount);

        assertThat(userPoint.getPoint()).isEqualTo(initialPoint - useAmount);
    }

    @Test
    @DisplayName("🔴 포인트_사용_테스트_포인트가_부족하면_INVALID_AMOUNT_VALUE_예외반환")
    void usePointTest_포인트_사용_예외_테스트_포인트가_부족하여_예외가_발생한다() {
        Long userId = 1L;
        int initialPoint = 50;
        int useAmount = 100;

        UserPoint userPoint = UserPoint.create(userId, initialPoint);

        assertThatThrownBy(() -> userPoint.usePoint(useAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_POINTS.getMessage());
    }

    @Test
    @DisplayName("🔴 포인트_사용_테스트_포인트가_유효하지_않으면_INVALID_AMOUNT_VALUE_예외반환")
    void usePointTest_포인트_충전_테스트_포인트가_유효하지_않으면_INVALID_AMOUNT_VALUE_예외반환() {
        Long userId = 1L;
        int initialPoint = 50;
        int useAmount = 0;

        UserPoint userPoint = UserPoint.create(userId, initialPoint);

        assertThatThrownBy(() -> userPoint.usePoint(useAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AMOUNT_VALUE.getMessage());
    }
}