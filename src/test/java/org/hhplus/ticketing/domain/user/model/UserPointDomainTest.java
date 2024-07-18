package org.hhplus.ticketing.domain.user.model;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserPointDomainTest {

    @Test
    @DisplayName("[성공테스트] 유저포인트_객체_생성_테스트_유저ID와_포인트로_객체가_생성된다")
    void createUserPointDomainTest_유저포인트_객체_생성_테스트_유저ID와_포인트로_객체가_생성된다() {
        Long userId = 1L;
        int point = 100;

        UserPointDomain userPointDomain = new UserPointDomain(userId, point);

        assertThat(userPointDomain.getUserId()).isEqualTo(userId);
        assertThat(userPointDomain.getPoint()).isEqualTo(point);
    }

    @Test
    @DisplayName("[성공테스트] 디폴트_유저포인트_객체_생성_테스트_포인트가_0인_객체가_생성된다")
    void defaultUserPointDomainTest_디폴트_유저포인트_객체_생성_테스트_포인트가_0인_객체가_생성된다() {
        Long userId = 1L;

        UserPointDomain userPointDomain = UserPointDomain.defaultUserPointDomain(userId);

        assertThat(userPointDomain.getUserId()).isEqualTo(userId);
        assertThat(userPointDomain.getPoint()).isEqualTo(0);
    }

    @Test
    @DisplayName("[성공테스트] 포인트_추가_테스트_포인트가_추가된다")
    void increasePointTest_포인트_추가_테스트_포인트가_추가된다() {
        Long userId = 1L;
        int initialPoint = 100;
        int addAmount = 50;

        UserPointDomain userPointDomain = new UserPointDomain(userId, initialPoint);
        userPointDomain.increasePoint(addAmount);

        assertThat(userPointDomain.getPoint()).isEqualTo(initialPoint + addAmount);
    }

    @Test
    @DisplayName("[성공테스트] 포인트_차감_테스트_포인트가_차감된다")
    void decreasePointTest_포인트_차감_테스트_포인트가_차감된다() {
        Long userId = 1L;
        int initialPoint = 100;
        int deductAmount = 50;

        UserPointDomain userPointDomain = new UserPointDomain(userId, initialPoint);
        userPointDomain.decreasePoint(deductAmount);

        assertThat(userPointDomain.getPoint()).isEqualTo(initialPoint - deductAmount);
    }

    @Test
    @DisplayName("[실패테스트] 포인트_차감_예외_테스트_포인트가_부족하여_예외가_발생한다")
    void decreasePointTest_포인트_차감_예외_테스트_포인트가_부족하여_예외가_발생한다() {
        Long userId = 1L;
        int initialPoint = 50;
        int deductAmount = 100;

        UserPointDomain userPointDomain = new UserPointDomain(userId, initialPoint);

        assertThatThrownBy(() -> userPointDomain.decreasePoint(deductAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_POINTS.getMessage());
    }
}