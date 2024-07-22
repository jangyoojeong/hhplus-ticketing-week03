package org.hhplus.ticketing.domain.queue.model;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class QueueTest {

    @Test
    @DisplayName("🟢 활성화_토큰_객체_생성_테스트_활성화_상태의_토큰_객체가_생성된다")
    void createActiveTest_활성화_토큰_객체_생성_테스트_활성화_상태의_토큰_객체가_생성된다() {
        // Given
        Long userId = 1L;

        // When
        Queue queue = Queue.createActive(userId);

        // Then
        assertThat(queue.getUserId()).isEqualTo(userId);
        assertThat(queue.getStatus()).isEqualTo(Queue.Status.ACTIVE);
        assertThat(queue.getEnteredAt()).isNotNull();
        assertThat(queue.getCreateAt()).isNotNull();
    }

    @Test
    @DisplayName("🟢 대기_토큰_객체_생성_테스트_대기_상태의_토큰_객체가_생성된다")
    void createWaitingTest_대기_토큰_객체_생성_테스트_대기_상태의_토큰_객체가_생성된다() {
        // Given
        Long userId = 1L;

        // When
        Queue queue = Queue.createWaiting(userId);

        // Then
        assertThat(queue.getUserId()).isEqualTo(userId);
        assertThat(queue.getStatus()).isEqualTo(Queue.Status.WAITING);
        assertThat(queue.getCreateAt()).isNotNull();
        assertThat(queue.getEnteredAt()).isNull();
    }

    @Test
    @DisplayName("🟢 토큰_만료_상태변경_테스트_만료_상태의_토큰으로_변경된다")
    void setExpiredTest_토큰_만료_상태변경_테스트_만료_상태의_토큰으로_변경된다() {
        // Given
        Long queueId = 1L;
        Long userId = 1L;
        Queue queue = Queue.create(queueId, userId, Queue.Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        // When
        Queue returnToken = queue.setExpired();

        // Then
        assertThat(returnToken.getQueueId()).isEqualTo(queue.getQueueId());
        assertThat(returnToken.getUserId()).isEqualTo(queue.getUserId());
        assertThat(returnToken.getToken()).isEqualTo(queue.getToken());
        assertThat(returnToken.getStatus()).isEqualTo(Queue.Status.EXPIRED);
        assertThat(returnToken.getEnteredAt()).isEqualTo(queue.getEnteredAt());
        assertThat(returnToken.getCreateAt()).isEqualTo(queue.getCreateAt());
    }

    @Test
    @DisplayName("🟢 토큰_활성화_상태변경_테스트_활성화_상태의_토큰으로_변경된다")
    void setActiveTest_토큰_활성화_상태변경_테스트_활성화_상태의_토큰으로_변경된다() {
        // Given
        Long queueId = 1L;
        Long userId = 1L;
        Queue queue = Queue.create(queueId, userId, Queue.Status.WAITING, LocalDateTime.now(), LocalDateTime.now());

        // When
        Queue returnToken = queue.setActive();

        // Then
        assertThat(returnToken.getQueueId()).isEqualTo(queueId);
        assertThat(returnToken.getUserId()).isEqualTo(userId);
        assertThat(returnToken.getStatus()).isEqualTo(Queue.Status.ACTIVE);
        assertThat(returnToken.getEnteredAt()).isNotNull();
    }

    @Test
    @DisplayName("🟢 대기열_순번_계산_테스트_마지막_활성화_토큰이_있는_경우")
    void getQueuePositionTest_대기열_순번_계산_테스트_마지막_활성화_토큰이_있는_경우() {

        // Given
        Long queueId = 1L;
        Long userId = 1L;
        Queue queue = Queue.create(2L, userId, Queue.Status.WAITING, LocalDateTime.now(), LocalDateTime.now());
        Queue lastActiveQueue = Queue.create(queueId, userId, Queue.Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        // When
        Long position = queue.getQueuePosition(Optional.of(lastActiveQueue));

        // Then
        assertThat(position).isEqualTo(queue.getQueueId() - lastActiveQueue.getQueueId());
    }

    @Test
    @DisplayName("🟢 대기열_순번_계산_테스트_마지막_활성화_토큰이_없는_경우")
    void getQueuePositionTest_대기열_순번_계산_테스트_마지막_활성화_토큰이_없는_경우() {

        // Given
        Long queueId = 1L;
        Long userId = 1L;
        Queue queue = Queue.create(queueId, userId,  Queue.Status.WAITING, LocalDateTime.now(), LocalDateTime.now());

        // When
        Long position = queue.getQueuePosition(Optional.empty());

        // Then
        assertThat(position).isEqualTo(0L);
    }

    @Test
    @DisplayName("🔴 토큰_유효성_테스트_토큰이_유효하지_않을_경우_INVALID_TOKEN_에러반환")
    void validateActiveStatusTest_토큰_유효성_테스트_토큰이_유효하지_않을_경우_INVALID_TOKEN_에러반환() {

        // Given
        Long queueId = 1L;
        Long userId = 1L;
        Queue queue = Queue.create(queueId, userId, Queue.Status.EXPIRED, LocalDateTime.now(), LocalDateTime.now());

        // When & Then
        assertThatThrownBy(queue::validateActiveStatus)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }
}