package org.hhplus.ticketing.domain.queue.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class QueueTest {

    @Test
    @DisplayName("🟢 토큰_객체_생성_테스트_토큰_객체가_생성된다")
    void createTest_토큰_객체_생성_테스트_토큰_객체가_생성된다() {

        // When
        Queue queue = Queue.create();

        // Then
        assertThat(queue.getToken()).isNotNull();
        assertThat(queue.getScore()).isNotNull();
    }

    @Test
    @DisplayName("🟢 순위계산_테스트_입력된_숫자에_1을_더한순위가_리턴된다")
    void getPositionTest_순위계산_테스트_입력된_숫자에_1을_더한순위가_리턴된다() {

        // When
        Long position = Queue.getPosition(0L);

        // Then
        assertThat(position).isEqualTo(0L + 1);
    }

    @Test
    @DisplayName("🟢 순위계산_테스트_null이_입력되면_0이_리턴된다")
    void getPositionTest_순위계산_테스트_null이_입력되면_0이_리턴된다() {

        // When
        Long position = Queue.getPosition(null);

        // Then
        assertThat(position).isEqualTo(0L);
    }

    @Test
    @DisplayName("🟢 잔여시간계산_테스트_순위_1L_넣으면_한_사이클의_시간_리턴_확인")
    void getRemainingWaitTimeTest_잔여시간계산_테스트_순위_1L_넣으면_한_사이클의_시간_리턴_확인() {

        // When
        String waitTime = Queue.getRemainingWaitTime(1L);

        // Then
        assertEquals("00시간 00분 10초", waitTime);
    }

    @Test
    @DisplayName("🟢 잔여시간계산_테스트_0이_입력되면_null반환")
    void getRemainingWaitTimeTest_잔여시간계산_테스트_0이_입력되면_null반환() {

        // When
        String waitTime = Queue.getRemainingWaitTime(0L);

        // Then
        assertNull(waitTime);
    }
}