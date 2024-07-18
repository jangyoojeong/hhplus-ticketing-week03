package org.hhplus.ticketing.domain.queue.model;

import org.hhplus.ticketing.domain.queue.model.enums.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class QueueDomainTest {
    @Test
    @DisplayName("[성공테스트] 활성화_토큰_객체_생성_테스트_활성화_상태의_토큰_객체가_생성된다")
    void createActiveQueueTest_활성화_토큰_객체_생성_테스트_활성화_상태의_토큰_객체가_생성된다() {
        Long userId = 1L;

        QueueDomain queueDomain = QueueDomain.createActiveQueue(userId);

        assertThat(queueDomain.getUserId()).isEqualTo(userId);
        assertThat(queueDomain.getStatus()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(queueDomain.getEnteredAt()).isNotNull();
        assertThat(queueDomain.getCreateAt()).isNotNull();
    }

    @Test
    @DisplayName("[성공테스트] 대기_토큰_객체_생성_테스트_대기_상태의_토큰_객체가_생성된다")
    void createWaitingQueueTest_대기_토큰_객체_생성_테스트_대기_상태의_토큰_객체가_생성된다() {
        Long userId = 1L;

        QueueDomain queueDomain = QueueDomain.createWaitingQueue(userId);

        assertThat(queueDomain.getUserId()).isEqualTo(userId);
        assertThat(queueDomain.getStatus()).isEqualTo(TokenStatus.WAITING);
        assertThat(queueDomain.getCreateAt()).isNotNull();
        assertThat(queueDomain.getEnteredAt()).isNull();
    }

    @Test
    @DisplayName("[성공테스트] 토큰_만료_상태변경_테스트_만료_상태의_토큰으로_변경된다")
    void updateQueueExpiredTest_토큰_만료_상태변경_테스트_만료_상태의_토큰으로_변경된다() {
        Long queueId = 1L;
        Long userId = 1L;
        UUID token = UUID.randomUUID();
        LocalDateTime enteredAt = LocalDateTime.now();

        QueueDomain queueDomain = QueueDomain.builder()
                .queueId(queueId)
                .userId(userId)
                .token(token)
                .status(TokenStatus.ACTIVE)
                .enteredAt(enteredAt)
                .build();

        QueueDomain updatedQueueDomain = queueDomain.updateQueueExpired();

        assertThat(updatedQueueDomain.getQueueId()).isEqualTo(queueId);
        assertThat(updatedQueueDomain.getUserId()).isEqualTo(userId);
        assertThat(updatedQueueDomain.getToken()).isEqualTo(token);
        assertThat(updatedQueueDomain.getStatus()).isEqualTo(TokenStatus.EXPIRED);
        assertThat(updatedQueueDomain.getEnteredAt()).isEqualTo(enteredAt);
    }

    @Test
    @DisplayName("[성공테스트] 토큰_활성화_상태변경_테스트_활성화_상태의_토큰으로_변경된다")
    void updateQueueActiveTest_토큰_활성화_상태변경_테스트_활성화_상태의_토큰으로_변경된다() {
        Long queueId = 1L;
        Long userId = 1L;
        UUID token = UUID.randomUUID();
        LocalDateTime enteredAt = LocalDateTime.now();

        QueueDomain queueDomain = QueueDomain.builder()
                .queueId(queueId)
                .userId(userId)
                .token(token)
                .status(TokenStatus.WAITING)
                .enteredAt(enteredAt)
                .build();

        QueueDomain updatedQueueDomain = queueDomain.updateQueueActive();

        assertThat(updatedQueueDomain.getQueueId()).isEqualTo(queueId);
        assertThat(updatedQueueDomain.getUserId()).isEqualTo(userId);
        assertThat(updatedQueueDomain.getToken()).isEqualTo(token);
        assertThat(updatedQueueDomain.getStatus()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(updatedQueueDomain.getEnteredAt()).isNotNull();
    }
}