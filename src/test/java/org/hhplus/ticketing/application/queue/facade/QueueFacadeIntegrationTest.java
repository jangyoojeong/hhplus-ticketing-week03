package org.hhplus.ticketing.application.queue.facade;

import org.hhplus.ticketing.domain.queue.QueueService;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.user.UserInfoService;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

// 대기열 파사드&서비스 통합테스트입니다.
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 모든 테스트가 독립적으로 실행되도록 보장
class QueueFacadeIntegrationTest {

    @Autowired
    private QueueFacade queueFacade;
    @MockBean
    private QueueService queueService;
    @MockBean
    private UserInfoService userInfoService;

    private Long userId;
    private UUID token;
    private QueueCommand.IssueTokenCommand issueTokenCommand;
    private QueueResult.IssueTokenResult issueTokenResult;
    private QueueResult.QueueStatusResult queueStatusResult;
    private UserResult.UserInfoResult userInfoResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = 1L;
        token = UUID.randomUUID();

        issueTokenCommand = QueueCommand.IssueTokenCommand.builder()
                .userId(userId)
                .build();

        issueTokenResult = QueueResult.IssueTokenResult.builder()
                .userId(userId)
                .token(token)
                .build();

        queueStatusResult = QueueResult.QueueStatusResult.builder()
                .userId(userId)
                .queuePosition(1L)
                .build();

        userInfoResult = UserResult.UserInfoResult.builder()
                .userId(userId)
                .userName("Test User")
                .build();

        given(userInfoService.validateUser(anyLong())).willReturn(userInfoResult);
        given(queueService.issueToken(any(QueueCommand.IssueTokenCommand.class))).willReturn(issueTokenResult);
        given(queueService.getQueueStatus(any(UUID.class))).willReturn(queueStatusResult);
        given(queueService.validateToken(any(UUID.class))).willReturn(true);
    }

    @Test
    @DisplayName("[성공테스트] 토큰_발급_통합_테스트")
    void issueTokenTest_토큰_발급_통합_테스트() {
        // When
        QueueResult.IssueTokenResult result = queueFacade.issueToken(issueTokenCommand);

        // Then
        assertNotNull(result);
        assertEquals(issueTokenCommand.getUserId(), result.getUserId());
        verify(userInfoService, times(1)).validateUser(issueTokenCommand.getUserId());
        verify(queueService, times(1)).issueToken(issueTokenCommand);
    }

    @Test
    @DisplayName("[성공테스트] 대기열_상태_조회_통합_테스트")
    void getQueueStatusTest_대기열_상태_조회_통합_테스트() {
        // When
        QueueResult.QueueStatusResult result = queueFacade.getQueueStatus(token);

        // Then
        assertNotNull(result);
        assertEquals(queueStatusResult.getUserId(), result.getUserId());
        verify(queueService, times(1)).getQueueStatus(token);
    }

    @Test
    @DisplayName("[성공테스트] 토큰_검증_통합_테스트")
    void validateToken_Success() {
        // When
        boolean isValid = queueFacade.validateToken(token);

        // Then
        assertTrue(isValid);
        verify(queueService, times(1)).validateToken(token);
    }

    @Test
    @DisplayName("[성공테스트] 대기열_상태_업데이트_통합_테스트")
    void updateQueueStatusesTest_대기열_상태_업데이트_통합_테스트() {
        // When
        queueFacade.updateQueueStatuses();

        // Then
        verify(queueService, times(1)).updateQueueStatuses();
    }

    @Test
    @DisplayName("[실패테스트] 토큰_발급_통합_테스트_유저정보_없음_예외발생")
    void issueTokenTest_토큰_발급_통합_테스트_유저정보_없음_예외발생() {
        // Given
        given(userInfoService.validateUser(anyLong()))
                .willThrow(new IllegalArgumentException("유저 정보가 존재하지 않습니다"));

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> queueFacade.issueToken(issueTokenCommand));
        assertEquals("유저 정보가 존재하지 않습니다", exception.getMessage());

        verify(userInfoService, times(1)).validateUser(issueTokenCommand.getUserId());
        verify(queueService, times(0)).issueToken(issueTokenCommand);
    }
}