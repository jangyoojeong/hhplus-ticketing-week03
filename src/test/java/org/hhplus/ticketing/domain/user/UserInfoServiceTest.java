package org.hhplus.ticketing.domain.user;

import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.user.model.UserInfo;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

// 유저 서비스 단위테스트입니다.
class UserInfoServiceTest {

    @InjectMocks
    private UserInfoService userInfoService;

    @Mock
    private UserInfoRepository userInfoRepository;

    private UserInfo userInfoDomain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userInfoDomain = UserInfo.builder()
                .userId(1L)
                .userName("사용자1")
                .build();
    }

    @Test
    @DisplayName("🟢 1L_유저_정보를_성공적으로_조회")
    void validateUserTest_1L_유저_정보를_성공적으로_조회() {

        // Given
        given(userInfoRepository.getUser(anyLong())).willReturn(Optional.of(userInfoDomain));

        // When
        UserResult.UserInfoResult result = userInfoService.validateUser(userInfoDomain.getUserId());

        // Then
        assertNotNull(result);
        assertEquals(userInfoDomain.getUserId(), result.getUserId());
    }

    @Test
    @DisplayName("🔴 1L_유저_정보가_없으면_USER_NOT_FOUND_예외반환")
    void validateUserTest_1L_유저_정보가_없을_때_예외_발생() {

        // Given
        given(userInfoRepository.getUser(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userInfoService.validateUser(userInfoDomain.getUserId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}