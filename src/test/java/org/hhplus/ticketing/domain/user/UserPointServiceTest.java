package org.hhplus.ticketing.domain.user;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.user.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    private Long userId;
    private UserPoint userPoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = 1L;

        userPoint = UserPoint.builder()
                .userId(userId)
                .point(50000)
                .build();
    }

    @Test
    @DisplayName("🟢 잔액_충전_테스트_기존_50000포인트에_20000포인트_충전_시_70000포인트를_반환한다")
    void addUserPointTest_잔액_충전_테스트_기존_50000포인트에_20000포인트_충전_시_70000포인트를_반환한다() {
        // Given
        int addAmount = 20000;
        UserCommand.ChargePoint command = new UserCommand.ChargePoint(userId, addAmount);

        given(userPointRepository.getUserPoint(anyLong())).willReturn(Optional.ofNullable(userPoint));
        given(userPointRepository.save(any(UserPoint.class))).willAnswer(invocation -> {
            UserPoint savedDomain = invocation.getArgument(0);
            savedDomain.setPoint(savedDomain.getPoint() + addAmount);
            return savedDomain;
        });

        // When
        UserResult.ChargePoint result = userPointService.chargePoint(command);

        // Then
        assertNotNull(result);
        assertEquals(userPoint.getPoint(), result.getPoint());
        verify(userPointRepository, times(1)).save(any(UserPoint.class));
        verify(userPointHistoryRepository, times(1)).save(any(UserPointHistory.class));
    }

    @Test
    @DisplayName("🟢 잔액_충전_테스트_기존_50000포인트에_20000포인트_차감_시_30000포인트를_반환한다")
    void useUserPointTest_잔액_충전_테스트_기존_50000포인트에_20000포인트_차감_시_30000포인트를_반환한다() {
        // Given
        int useAmount = 20000;
        UserCommand.UsePoint command = new UserCommand.UsePoint(userId, useAmount);

        given(userPointRepository.getUserPoint(anyLong())).willReturn(Optional.ofNullable(userPoint));
        given(userPointRepository.save(any(UserPoint.class))).willAnswer(invocation -> {
            UserPoint savedDomain = invocation.getArgument(0);
            savedDomain.setPoint(savedDomain.getPoint() - useAmount);
            return savedDomain;
        });

        // When
        UserResult.UsePoint result = userPointService.usePoint(command);

        // Then
        assertNotNull(result);
        assertEquals(userPoint.getPoint(), result.getPoint());
        verify(userPointRepository, times(1)).save(any(UserPoint.class));
        verify(userPointHistoryRepository, times(1)).save(any(UserPointHistory.class));
    }

    @Test
    @DisplayName("🔴 잔액_부족_테스트_기존_50000포인트에_200000포인트_차감_시_INSUFFICIENT_POINTS_예외반환")
    void useUserPointTest_잔액_부족_테스트_기존_50000포인트에_200000포인트_차감_시_INSUFFICIENT_POINTS_예외반환() {
        // Given
        int useAmount = 200000;
        UserCommand.UsePoint command = new UserCommand.UsePoint(userId, useAmount);

        given(userPointRepository.getUserPoint(anyLong())).willReturn(Optional.ofNullable(userPoint));

        // When & Then
        assertThatThrownBy(() -> userPointService.usePoint(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INSUFFICIENT_POINTS);
    }

    @Test
    @DisplayName("🟢 잔액_조회_테스트_1L유저_잔액_조회_시_50000포인트를_반환한다")
    void getUserPointTest_잔액_조회_테스트_1L유저_잔액_조회_시_50000포인트를_반환한다() {
        // Given
        given(userPointRepository.getUserPoint(anyLong())).willReturn(Optional.ofNullable(userPoint));

        // When
        UserResult.GetPoint result = userPointService.getPointResult(userId);

        // Then
        assertNotNull(result);
        assertEquals(userPoint.getPoint(), result.getPoint());
    }
}