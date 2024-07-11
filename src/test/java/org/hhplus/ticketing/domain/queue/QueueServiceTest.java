package org.hhplus.ticketing.domain.queue;

import org.hhplus.ticketing.domain.queue.model.QueueCommand;
import org.hhplus.ticketing.domain.queue.model.QueueDomain;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.queue.model.enums.TokenStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

// 대기열 서비스 단위테스트입니다.
public class QueueServiceTest {

    @InjectMocks
    private QueueService queueService;

    @Mock
    private QueueRepository queueRepository;

    private QueueDomain queueDomain;
    private UUID token;

    private LocalDateTime fixedNow;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        token = UUID.randomUUID();

        queueDomain = QueueDomain.builder()
                .queueId(1L)
                .userId(1L)
                .token(token)
                .status(TokenStatus.WAITING)
                .enteredAt(LocalDateTime.now())
                .build();

        fixedNow = LocalDateTime.of(2024, 7, 12, 7, 0);
    }

    @Test
    @DisplayName("[성공테스트] 토큰_발급_테스트")
    void issueTokenTest_토큰_발급_테스트() {

        // Given
        QueueCommand.IssueTokenCommand command = new QueueCommand.IssueTokenCommand(1L);
        given(queueRepository.save(any(QueueDomain.class))).willReturn(queueDomain);

        // When
        QueueResult.IssueTokenResult result = queueService.issueToken(command);

        // Then
        assertNotNull(result);
        assertEquals(token, result.getToken());
        verify(queueRepository, times(1)).save(any(QueueDomain.class));
    }

    @Test
    @DisplayName("[성공테스트] 대기열_상태_조회_테스트")
    void getQueueStatusTest_대기열_상태_조회_테스트() {

        // Given
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.of(queueDomain));
        given(queueRepository.getLastActiveQueue(TokenStatus.ACTIVE)).willReturn(Optional.empty());

        // When
        QueueResult.QueueStatusResult result = queueService.getQueueStatus(token);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getQueuePosition());
        verify(queueRepository, times(1)).findByToken(token);
    }

    @Test
    @DisplayName("[실패테스트] 대기열_상태_조회_테스트_토큰_정보_없으면_예외반환")
    void getQueueStatusTest_대기열_상태_조회_테스트_토큰_정보_없으면_예외반환() {

        // Given
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> queueService.getQueueStatus(token));
        assertEquals("토큰 정보가 존재하지 않습니다.", exception.getMessage());
    }
    @Test
    @DisplayName("[성공테스트] 토큰_검증_테스트_유효한_토큰")
    void validateTokenTest_토큰_검증_테스트_유효한토큰() {

        // Given
        queueDomain.setStatus(TokenStatus.ACTIVE);
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.of(queueDomain));

        // When
        boolean isValid = queueService.validateToken(token);

        // Then
        assertTrue(isValid);
    }
    @Test
    @DisplayName("[성공테스트] 토큰_검증_테스트_유효_하지_않은_토큰")
    void validateTokenTest_토큰_검증_테스트_유효하지않은_토큰() {

        // Given
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.of(queueDomain));

        // When
        boolean isValid = queueService.validateToken(token);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("[실패테스트] 토큰_검증_테스트_토큰_검증_시_토큰_정보_없음")
    void validateTokenTest_토큰_검증_테스트_토큰_검증_시_토큰_정보_없음() {

        // Given
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> queueService.validateToken(token));
        assertEquals("토큰 정보가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("[성공테스트] 토큰_만료_테스트")
    void expireTokenTest_토큰_만료_테스트() {

        // Given
        queueDomain.setStatus(TokenStatus.ACTIVE);
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.of(queueDomain));
        given(queueRepository.save(any(QueueDomain.class))).willReturn(queueDomain);

        // When
        QueueResult.expireTokenResult result = queueService.expireToken(token);

        // Then
        assertNotNull(result);
        verify(queueRepository, times(1)).save(any(QueueDomain.class));
    }

    @Test
    @DisplayName("[실패테스트] 토큰_만료_테스트_토큰_정보_없음")
    void expireTokenTest_토큰_만료_테스트_토큰_정보_없음() {

        // Given
        given(queueRepository.findByToken(any(UUID.class))).willReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> queueService.expireToken(token));
        assertEquals("토큰 정보가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("[성공테스트] 대기열_상태_업데이트_테스트")
    void updateQueueStatusesTest_대기열_상태_업데이트_테스트() {
        // Given
        QueueDomain activeToken = QueueDomain.builder()
                .userId(1L)
                .token(UUID.randomUUID())
                .status(TokenStatus.ACTIVE)
                .enteredAt(fixedNow.minusMinutes(8))
                .build();
        QueueDomain waitingToken = QueueDomain.builder()
                .userId(2L)
                .token(UUID.randomUUID())
                .status(TokenStatus.WAITING)
                .enteredAt(fixedNow)
                .build();

        List<QueueDomain> activeTokensToExpire = Arrays.asList(activeToken);
        List<QueueDomain> waitingTokensToActivate = Arrays.asList(waitingToken);

        given(queueRepository.findActiveTokensEnteredBefore(eq(TokenStatus.ACTIVE), any(LocalDateTime.class)))
                .willReturn(activeTokensToExpire);
        given(queueRepository.countByStatus(TokenStatus.ACTIVE)).willReturn(1L);
        given(queueRepository.countByStatus(TokenStatus.WAITING)).willReturn(1L);
        given(queueRepository.findByStatusOrderByCreatedAtAsc(eq(TokenStatus.WAITING), any(Pageable.class)))
                .willReturn(waitingTokensToActivate);

        doAnswer(invocation -> {
            List<QueueDomain> arg = invocation.getArgument(0);
            assertThat(arg).hasSize(1);
            assertThat(arg.get(0).getStatus()).isEqualTo(TokenStatus.EXPIRED);
            return null;
        }).when(queueRepository).saveAll(argThat(list -> list.get(0).getStatus() == TokenStatus.EXPIRED));

        doAnswer(invocation -> {
            List<QueueDomain> arg = invocation.getArgument(0);
            assertThat(arg).hasSize(1);
            assertThat(arg.get(0).getStatus()).isEqualTo(TokenStatus.ACTIVE);
            return null;
        }).when(queueRepository).saveAll(argThat(list -> list.get(0).getStatus() == TokenStatus.ACTIVE));

        // When
        queueService.updateQueueStatuses();

        // Then
        verify(queueRepository, times(1)).findActiveTokensEnteredBefore(eq(TokenStatus.ACTIVE), any(LocalDateTime.class));
        verify(queueRepository, times(1)).findByStatusOrderByCreatedAtAsc(eq(TokenStatus.WAITING), any(Pageable.class));
        verify(queueRepository, times(1)).countByStatus(TokenStatus.ACTIVE);
    }
}
