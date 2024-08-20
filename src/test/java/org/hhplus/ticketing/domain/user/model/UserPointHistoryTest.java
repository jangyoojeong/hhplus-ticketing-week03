package org.hhplus.ticketing.domain.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserPointHistoryTest {
    @Test
    @DisplayName("🟢 [유저포인트이력_객체_생성_테스트]")
    void createTest_유저ID_포인트_타입으로_객체가_생성된다() {
        Long userId = 1L;
        int amount = 100;
        UserPointHistory.Type type = UserPointHistory.Type.CHARGE;

        UserPointHistory history = UserPointHistory.create(userId, amount, type);

        assertThat(history.getUserId()).isEqualTo(userId);
        assertThat(history.getAmount()).isEqualTo(amount);
        assertThat(history.getType()).isEqualTo(type);
    }
}