package org.hhplus.ticketing.interfaces.controller.user;

import org.hhplus.ticketing.application.user.UserFacade;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.hhplus.ticketing.interfaces.controller.user.dto.UserRequest;
import org.hhplus.ticketing.interfaces.controller.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

// 유저 컨트롤러 단위테스트입니다.
public class UserControllerUnitTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserFacade userFacade;

    private Long userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
    }

    @Test
    @DisplayName("🟢 잔액_충전_컨트롤러_테스트_기존_0포인트_충전_후_50000_포인트_리턴_확인")
    void addUserPointTest_잔액_충전_컨트롤러_테스트_기존_0포인트_충전_후_50000_포인트_리턴_확인 () throws Exception {
        // Given
        int currentPoints = 0;

        UserRequest.ChargePointRequest request = new UserRequest.ChargePointRequest(userId, 50000);
        UserResult.ChargePointResult result = new UserResult.ChargePointResult(userId, currentPoints + 50000);
        UserResponse.ChargePointResponse response = UserResponse.ChargePointResponse.from(result);

        given(userFacade.chargePoint(any(UserCommand.ChargePointCommand.class))).willReturn(result);

        // When
        ResponseEntity<UserResponse.ChargePointResponse> responseEntity = userController.addUserPoint(request);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }
    
    @Test
    @DisplayName("🟢 잔액_충전_컨트롤러_테스트_조회_후_50000_포인트_리턴_확인")
    void getUserPointTest_잔액_충전_컨트롤러_테스트_조회_후_50000_포인트_리턴_확인 () throws Exception {
        // Given
        UserResult.UserPointResult result = new UserResult.UserPointResult(userId, 50000);
        UserResponse.UserPointResponse response = UserResponse.UserPointResponse.from(result);

        given(userFacade.getPointResult(userId)).willReturn(result);

        // When
        ResponseEntity<UserResponse.UserPointResponse> responseEntity = userController.getUserPoint(userId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }
}