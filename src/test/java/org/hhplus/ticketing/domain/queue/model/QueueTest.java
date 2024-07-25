package org.hhplus.ticketing.domain.queue.model;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class QueueTest {

    @Test
    @DisplayName("🟢 활성화_토큰_객체_생성_테스트_활성화_상태의_토큰_객체가_생성된다")
    void createActiveTest_활성화_토큰_객체_생성_테스트_활성화_상태의_토큰_객체가_생성된다() {
        // Given
        Long userId = 1L;

        // When
        Queue queue = Queue.create(0L, userId);

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
        Queue queue = Queue.create((long) QueueConstants.MAX_ACTIVE_USERS, userId);

        // Then
        assertThat(queue.getUserId()).isEqualTo(userId);
        assertThat(queue.getStatus()).isEqualTo(Queue.Status.WAITING);
        assertThat(queue.getCreateAt()).isNotNull();
        assertThat(queue.getEnteredAt()).isNull();
    }

    @Test
    @DisplayName("🟢 대기열_순번_계산_테스트_마지막_활성화_토큰이_있는_경우")
    void getQueuePositionTest_대기열_순번_계산_테스트_마지막_활성화_토큰이_있는_경우() {

        // Given
        Long queueId = 1L;
        Long userId = 1L;
        Queue queue = Queue.builder()
                .queueId(2L)
                .userId(userId)
                .token(UUID.randomUUID())
                .status(Queue.Status.WAITING)
                .createAt(LocalDateTime.now())
                .build();
        Queue lastActiveQueue = Queue.builder()
                .queueId(queueId)
                .userId(userId)
                .token(UUID.randomUUID())
                .status(Queue.Status.ACTIVE)
                .enteredAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .build();

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
        Queue queue = Queue.builder()
                .queueId(queueId)
                .userId(userId)
                .token(UUID.randomUUID())
                .status(Queue.Status.WAITING)
                .createAt(LocalDateTime.now())
                .build();

        // When
        Long position = queue.getQueuePosition(Optional.empty());

        // Then
        assertThat(position).isEqualTo(0L);
    }

    @Test
    @DisplayName("🟢 토큰_만료_상태변경_테스트_만료_상태의_토큰으로_변경된다")
    void setExpiredTest_토큰_만료_상태변경_테스트_만료_상태의_토큰으로_변경된다() {
        // Given
        Long queueId = 1L;
        Long userId = 1L;
        Queue queue = Queue.builder()
                .queueId(queueId)
                .userId(userId)
                .token(UUID.randomUUID())
                .status(Queue.Status.ACTIVE)
                .enteredAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .build();

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
        Queue queue = Queue.builder()
                .queueId(queueId)
                .userId(userId)
                .token(UUID.randomUUID())
                .status(Queue.Status.WAITING)
                .enteredAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .build();

        // When
        Queue returnToken = queue.setActive();

        // Then
        assertThat(returnToken.getQueueId()).isEqualTo(queueId);
        assertThat(returnToken.getUserId()).isEqualTo(userId);
        assertThat(returnToken.getStatus()).isEqualTo(Queue.Status.ACTIVE);
        assertThat(returnToken.getEnteredAt()).isNotNull();
    }

    @Test
    @DisplayName("🔴 토큰_유효성_테스트_토큰이_유효하지_않을_경우_INVALID_TOKEN_에러반환")
    void validateStatusTest_토큰_유효성_테스트_토큰이_유효하지_않을_경우_INVALID_TOKEN_에러반환() {

        // Given
        Long queueId = 1L;
        Long userId = 1L;
        Queue queue = Queue.builder()
                .queueId(queueId)
                .userId(userId)
                .token(UUID.randomUUID())
                .status(Queue.Status.EXPIRED)
                .enteredAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .build();

        // When & Then
        assertThatThrownBy(queue::validateStatus)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }
}