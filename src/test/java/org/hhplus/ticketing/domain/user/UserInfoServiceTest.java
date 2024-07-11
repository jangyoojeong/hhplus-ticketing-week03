package org.hhplus.ticketing.domain.user;

import org.hhplus.ticketing.domain.user.model.UserInfoDomain;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

// 유저 서비스 단위테스트입니다.
class UserInfoServiceTest {

    @InjectMocks
    private UserInfoService userInfoService;

    @Mock
    private UserInfoRepository userInfoRepository;

    private UserInfoDomain userInfoDomain;
    private UserResult.UserInfoResult userInfoResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userInfoDomain = UserInfoDomain.builder()
                .userId(1L)
                .userName("사용자1")
                .build();

        userInfoResult = UserResult.UserInfoResult.from(userInfoDomain);
    }

    @Test
    @DisplayName("[성공테스트] 1L_유저_정보를_성공적으로_조회")
    void validateUserTest_1L_유저_정보를_성공적으로_조회() {

        // Given
        given(userInfoRepository.findById(anyLong())).willReturn(Optional.of(userInfoDomain));

        // When
        UserResult.UserInfoResult result = userInfoService.validateUser(userInfoDomain.getUserId());

        // Then
        assertEquals(userInfoResult, result);
    }

    @Test
    @DisplayName("[실패테스트] 1L_유저_정보가_없을_때_예외_발생")
    void validateUserTest_1L_유저_정보가_없을_때_예외_발생() {

        // Given
        given(userInfoRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userInfoService.validateUser(userInfoDomain.getUserId()));
        assertEquals("유저 정보가 존재하지 않습니다", exception.getMessage());
    }
}