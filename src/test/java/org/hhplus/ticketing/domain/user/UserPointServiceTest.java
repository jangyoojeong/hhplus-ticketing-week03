package org.hhplus.ticketing.domain.user;

import org.hhplus.ticketing.domain.user.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

// 유저포인트 서비스 단위테스트입니다.
class UserPointServiceTest {

    @InjectMocks
    private UserPointService userPointService;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private UserPointHistoryRepository userPointHistoryRepository;

    private UserPointDomain userPointDomain;

    private Long userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = 1L;

        userPointDomain = UserPointDomain.builder()
                .userId(1L)
                .point(50000)
                .build();
    }

    @Test
    @DisplayName("[성공테스트] 잔액_충전_테스트_기존_50000포인트에_20000포인트_충전_시_70000포인트가_리턴된다")
    void addUserPointTest_잔액_충전_테스트_기존_50000포인트에_20000포인트_충전_시_70000포인트가_리턴된다() {
        // Given
        int addAmount = 20000;

        UserCommand.AddPointCommand command = new UserCommand.AddPointCommand(userId, addAmount);
        given(userPointRepository.findByUserId(anyLong())).willReturn(userPointDomain);
        given(userPointRepository.save(any(UserPointDomain.class))).willReturn(userPointDomain);

        // When
        UserResult.AddPointResult result = userPointService.addUserPoint(command);

        // Then
        assertNotNull(result);
        assertEquals(70000, result.getPoint());
        verify(userPointRepository, times(1)).save(any(UserPointDomain.class));
        verify(userPointHistoryRepository, times(1)).save(any(UserPointHistoryDomain.class));
    }

    @Test
    @DisplayName("[성공테스트] 잔액_충전_테스트_기존_50000포인트에_20000포인트_차감_시_30000포인트가_리턴된다")
    void useUserPointTest_잔액_충전_테스트_기존_50000포인트에_20000포인트_차감_시_30000포인트가_리턴된다() {
        // Given
        int useAmount = 20000;

        UserCommand.UsePointCommand command = new UserCommand.UsePointCommand(userId, useAmount);
        given(userPointRepository.findByUserId(anyLong())).willReturn(userPointDomain);
        given(userPointRepository.save(any(UserPointDomain.class))).willReturn(userPointDomain);

        // When
        UserResult.UsePointResult result = userPointService.useUserPoint(command);

        // Then
        assertNotNull(result);
        assertEquals(30000, result.getPoint());
        verify(userPointRepository, times(1)).save(any(UserPointDomain.class));
        verify(userPointHistoryRepository, times(1)).save(any(UserPointHistoryDomain.class));
    }

    @Test
    @DisplayName("[실패테스트] 잔액_부족_테스트_기존_50000포인트에_200000포인트_차감_시_예외_발생")
    void useUserPointTest_잔액_부족_테스트_기존_50000포인트에_200000포인트_차감_시_예외_발생() {
        // Given
        int useAmount = 200000;

        UserCommand.UsePointCommand command = new UserCommand.UsePointCommand(userId, useAmount);
        given(userPointRepository.findByUserId(anyLong())).willReturn(userPointDomain);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userPointService.useUserPoint(command));
        assertEquals("포인트가 부족합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("[성공테스트] 잔액_조회_테스트_1L유저_잔액_조회_시_50000포인트가_리턴된다")
    void getUserPointTest_잔액_조회_테스트_1L유저_잔액_조회_시_50000포인트가_리턴된다() {
        // Given
        given(userPointRepository.findByUserId(anyLong())).willReturn(userPointDomain);

        // When
        UserResult.UserPointResult result = userPointService.getUserPoint(userId);

        // Then
        assertNotNull(result);
        assertEquals(50000, result.getPoint());
    }
}