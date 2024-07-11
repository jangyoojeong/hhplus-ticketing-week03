package org.hhplus.ticketing.application.user.facade;


import org.hhplus.ticketing.domain.user.UserInfoService;
import org.hhplus.ticketing.domain.user.UserPointService;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

// 유저 파사드&서비스 통합테스트입니다.
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserFacadeIntegrationTest {

    @Autowired
    private UserFacade userFacade;
    @MockBean
    private UserPointService userPointService;
    @MockBean
    private UserInfoService userInfoService;

    private Long userId;
    private UserCommand.AddPointCommand addPointCommand;
    private UserResult.UserInfoResult userInfoResult;
    private UserResult.AddPointResult addPointResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = 1L;

        addPointCommand = UserCommand.AddPointCommand.builder()
                .userId(userId)
                .amount(5000)
                .build();

        userInfoResult = UserResult.UserInfoResult.builder()
                .userId(userId)
                .userName("John Doe")
                .build();

        addPointResult = UserResult.AddPointResult.builder()
                .userId(userId)
                .point(5000)
                .build();

        given(userInfoService.validateUser(anyLong())).willReturn(userInfoResult);
        given(userPointService.addUserPoint(any(UserCommand.AddPointCommand.class))).willReturn(addPointResult);
    }

    @Test
    @DisplayName("[성공테스트] 잔액 충전 통합 테스트")
    void addUserPoint_Success() {
        // When
        UserResult.AddPointResult result = userFacade.addUserPoint(addPointCommand);

        // Then
        assertNotNull(result);
        assertEquals(addPointCommand.getUserId(), result.getUserId());
        assertEquals(addPointCommand.getAmount(), result.getPoint());

        verify(userInfoService, times(1)).validateUser(userId);
        verify(userPointService, times(1)).addUserPoint(addPointCommand);
    }

    @Test
    @DisplayName("[성공테스트] 잔액 조회 통합 테스트")
    void getUserPoint_Success() {
        // Given
        UserResult.UserPointResult userPointResult = UserResult.UserPointResult.builder()
                .userId(userId)
                .point(5000)
                .build();

        given(userPointService.getUserPoint(anyLong())).willReturn(userPointResult);

        // When
        UserResult.UserPointResult result = userFacade.getUserPoint(userId);

        // Then
        assertNotNull(result);
        assertEquals(userPointResult.getUserId(), result.getUserId());
        assertEquals(userPointResult.getPoint(), result.getPoint());

        verify(userPointService, times(1)).getUserPoint(userId);
    }

    @Test
    @DisplayName("[실패테스트] 잔액 충전 통합 테스트 - 유저 정보 없음")
    void addUserPoint_Failure_UserNotFound() {
        // Given
        given(userInfoService.validateUser(anyLong())).willThrow(new IllegalArgumentException("유저 정보가 존재하지 않습니다"));

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userFacade.addUserPoint(addPointCommand));
        assertEquals("유저 정보가 존재하지 않습니다", exception.getMessage());

        verify(userInfoService, times(1)).validateUser(userId);
        verify(userPointService, times(0)).addUserPoint(addPointCommand);
    }
}