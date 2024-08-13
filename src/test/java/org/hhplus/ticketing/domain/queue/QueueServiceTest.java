package org.hhplus.ticketing.domain.queue;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    @DisplayName("🟢 토큰_발급_테스트_토큰이_발급되고_토큰순위_0L_리턴시_1을_더한_1L이_리턴된다")
    void issueTokenTest_토큰_발급_테스트_토큰이_발급되고_토큰순위_0L_리턴시_1을_더한_1L이_리턴된다() {
        // Given
        given(queueRepository.countActiveTokens()).willReturn(QueueConstants.MAX_ACTIVE_USERS);
        given(queueRepository.getWaitingPosition(anyString())).willReturn(0L);

        // When
        Queue result = queueService.issueToken();

        // Then
        assertNotNull(result);
        assertNotNull(result.getToken());
    }

    @Test
    @DisplayName("🟢 대기순번_테스트_조회된_0L에서_1을_더한_1L이_리턴된다")
    void getWaitingPositionTest_대기순번_테스트_조회된_0L에서_1을_더한_1L이_리턴된다() {
        // Given
        String token = UUID.randomUUID().toString();
        given(queueRepository.getWaitingPosition(anyString())).willReturn(0L);

        // When
        Long position = queueService.getWaitingPosition(token);

        // Then
        assertEquals(1L, position);
    }

    @Test
    @DisplayName("🟢 토큰_검증_테스트_유효한_토큰일경우")
    void validateTokenTest_토큰_검증_테스트_유효한_토큰일경우() {
        // Given
        String token = UUID.randomUUID().toString();
        given(queueRepository.isValid(anyString())).willReturn(true);

        // When & Then
        assertDoesNotThrow(() -> queueService.validateToken(token));
    }

    @Test
    @DisplayName("🔴 토큰_검증_테스트_유효하지_않은_토큰일경우_INVALID_TOKEN_예외반환")
    void validateTokenTest_토큰_검증_테스트_유효하지_않은_토큰일경우() {
        // Given
        String token = UUID.randomUUID().toString();
        given(queueRepository.isValid(anyString())).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> queueService.validateToken(token))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("🟢 토큰_만료_테스트_삭제메소드가_정상적으로_실행된다")
    void expireTokenTest_토큰_만료_테스트_삭제메소드가_정상적으로_실행된다() {
        // Given
        String token = UUID.randomUUID().toString();
        given(queueRepository.isValid(anyString())).willReturn(true);

        // When
        queueService.expireToken(token);

        // Then
        verify(queueRepository, times(1)).delActive(token);
    }

    @Test
    @DisplayName("🟢 토큰_활성화_테스트_활성화_대상토큰_있으면_활성화메소드가_정상적으로_실행된다")
    void activateTest_토큰_활성화_테스트_활성화_대상토큰_있으면_활성화메소드가_정상적으로_실행된다() {
        // Given
        Set<String> tokens = new HashSet<>();
        tokens.add(UUID.randomUUID().toString());
        tokens.add(UUID.randomUUID().toString());
        given(queueRepository.getActivatableTokens(anyLong(), anyLong())).willReturn(tokens);

        // When
        queueService.activate();

        // Then
        verify(queueRepository, times(1)).activate(tokens);
    }

    @Test
    @DisplayName("🟢 토큰_활성화_테스트_활성화_대상토큰_없으면_활성화메소드가_실행되지_않는다")
    void activateTest_토큰_활성화_테스트_활성화_대상토큰_없으면_활성화메소드가_실행되지_않는다() {
        // Given
        Set<String> tokens = new HashSet<>();
        given(queueRepository.getActivatableTokens(anyLong(), anyLong())).willReturn(tokens);

        // When
        queueService.activate();

        // Then
        verify(queueRepository, times(0)).activate(tokens);
    }
}
