package org.hhplus.ticketing.domain.queue;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

// 대기열 서비스 단위테스트입니다.
public class QueueServiceTest {

    @InjectMocks
    private QueueService queueService;
    @Mock
    private QueueRepository queueRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("🟢 토큰_발급_테스트_발급된_토큰을_정상적으로_반환한다")
    void issueTokenTest_토큰_발급_테스트_발급된_토큰이_정상적으로_반환된다() {

        // Given
        Long userId = 1L;
        Queue queue = Queue.createActive(userId);
        given(queueRepository.save(any(Queue.class))).willReturn(queue);

        // When
        QueueCommand.IssueTokenCommand command = new QueueCommand.IssueTokenCommand(userId);
        QueueResult.IssueTokenResult result = queueService.issueToken(command);

        // Then
        assertNotNull(result);
        assertEquals(queue.getToken(), result.getToken());
        verify(queueRepository, times(1)).save(any(Queue.class));
    }

    @Test
    @DisplayName("🟢 대기열_상태_조회_테스트_대기열_정보를_정상적으로_반환한다")
    void getQueueStatusTest_대기열_상태_조회_테스트_대기열_정보를_정상적으로_반환한다() {

        // Given
        Queue queue = Queue.createActive(1L);
        queue.setToken(UUID.randomUUID());
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.of(queue));
        given(queueRepository.getLastActiveQueue(Queue.Status.ACTIVE)).willReturn(Optional.empty());

        // When
        QueueResult.QueueStatusResult result = queueService.getQueueStatus(queue.getToken());

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getPosition());
        verify(queueRepository, times(1)).findByToken(queue.getToken());
    }

    @Test
    @DisplayName("🔴 대기열_상태_조회_테스트_토큰_정보_없으면_TOKEN_NOT_FOUND_예외반환")
    void getQueueStatusTest_대기열_상태_조회_테스트_토큰_정보_없으면_예외반환() {

        // Given
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> queueService.getQueueStatus(UUID.randomUUID()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOKEN_NOT_FOUND);
    }

    @Test
    @DisplayName("🔴 토큰_검증_테스트_WAITING_토큰은_INVALID_TOKEN_예외반환")
    void validateTokenTest_토큰_검증_테스트_유효한토큰() {

        // Given
        Queue queue = Queue.createWaiting(1L);
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.of(queue));

        // When & Then
        assertThatThrownBy(() -> queueService.validateToken(queue.getToken()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("🔴 토큰_검증_테스트_토큰_정보_없으면_TOKEN_NOT_FOUND_예외반환")
    void validateTokenTest_토큰_검증_테스트_토큰_정보_없으면_TOKEN_NOT_FOUND_예외반환() {

        // Given
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> queueService.validateToken(UUID.randomUUID()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOKEN_NOT_FOUND);
    }

    @Test
    @DisplayName("🟢 토큰_만료_테스트_토큰만료_후_결과를_정상적으로_반환한다")
    void expireTokenTest_토큰_만료_테스트_토큰만료_후_결과를_정상적으로_반환한다() {

        // Given
        Queue queue = Queue.createActive(1L);
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.of(queue));
        given(queueRepository.save(any(Queue.class))).willReturn(queue);

        // When
        QueueResult.expireTokenResult result = queueService.expireToken(queue.getToken());

        // Then
        assertNotNull(result);
        verify(queueRepository, times(1)).findByToken(any(UUID.class));
        verify(queueRepository, times(1)).save(any(Queue.class));
    }

    @Test
    @DisplayName("🔴 토큰_만료_테스트_토큰_정보_없으면_TOKEN_NOT_FOUND_예외반환")
    void expireTokenTest_토큰_만료_테스트_토큰_정보_없으면_TOKEN_NOT_FOUND_예외반환() {

        // Given
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> queueService.expireToken(UUID.randomUUID()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOKEN_NOT_FOUND);
    }

    @Test
    @DisplayName("🟢 대기열_상태_업데이트_테스트_만료대상_토큰_없으면_바로_종료된다")
    void refreshQueueTest_대기열_상태_업데이트_테스트_만료대상_토큰_없으면_바로_종료된다() {

        // Given
        given(queueRepository.getExpiredTokens(any(), any())).willReturn(Collections.emptyList());

        // When
        queueService.refreshQueue();

        // Then
        verify(queueRepository, times(1)).getExpiredTokens(any(), any());
        verify(queueRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("🟢 대기열_상태_업데이트_테스트_만료대상_토큰_있으면_저장로직이_정상적으로_실행된다")
    void refreshQueueTest_대기열_상태_업데이트_테스트_만료대상_토큰_있으면_저장로직이_정상적으로_실행된다() {

        // Given
        Queue queue = mock(Queue.class);
        List<Queue> expiredQueues = List.of(queue);
        given(queueRepository.getExpiredTokens(any(), any())).willReturn(expiredQueues);
        given(queueRepository.saveAll(anyList())).willReturn(expiredQueues);

        // When
        queueService.refreshQueue();

        // Then
        verify(queueRepository, times(1)).getExpiredTokens(any(), any());
        verify(queueRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("🟢 대기열_상태_업데이트_테스트_활성화_가능한_슬롯이_없으면_바로_종료된다")
    void refreshQueueTest_대기열_상태_업데이트_테스트_활성화_가능한_슬롯이_없으면_바로_종료된다() {

        // Given
        Long maxActiveUsers = (long) QueueConstants.MAX_ACTIVE_USERS;
        given(queueRepository.countByStatus(any())).willReturn(maxActiveUsers);

        // When
        queueService.refreshQueue();

        // Then
        verify(queueRepository, times(1)).countByStatus(any());
        verify(queueRepository, never()).getActivatableTokens(any(), any(Pageable.class));
    }
    @Test
    @DisplayName("🟢 대기열_상태_업데이트_테스트_활성화_가능한_슬롯이_있지만_대기중인_토큰이_없으면_바로_종료된다")
    void refreshQueueTest_대기열_상태_업데이트_테스트_활성화_가능한_슬롯이_있지만_대기중인_토큰이_없으면_바로_종료된다() {

        // Given
        given(queueRepository.getActivatableTokens(any(), any(Pageable.class))).willReturn(Collections.emptyList());

        // When
        queueService.refreshQueue();

        // Then
        verify(queueRepository, times(1)).countByStatus(any());
        verify(queueRepository, times(1)).getActivatableTokens(any(), any(Pageable.class));
        verify(queueRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("🟢 대기열_상태_업데이트_테스트_활성화_가능한_슬롯이_있고_대기중인_토큰이_있으면_정상적으로_활성화된다")
    void refreshQueueTest_대기열_상태_업데이트_테스트_활성화_가능한_슬롯이_있고_대기중인_토큰이_있으면_정상적으로_활성화된다() {

        // Given
        Queue queue = mock(Queue.class);
        List<Queue> queuesToActivate = List.of(queue);
        given(queueRepository.getActivatableTokens(any(), any(Pageable.class))).willReturn(queuesToActivate);
        given(queueRepository.saveAll(anyList())).willReturn(queuesToActivate);

        // When
        queueService.refreshQueue();

        // Then
        verify(queueRepository, times(1)).countByStatus(any());
        verify(queueRepository, times(1)).getActivatableTokens(any(), any(Pageable.class));
        verify(queueRepository, times(1)).saveAll(anyList());
    }

}
