package org.hhplus.ticketing.integration;

import org.hhplus.ticketing.application.user.UserCriteria;
import org.hhplus.ticketing.application.user.UserFacade;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.user.model.UserInfo;
import org.hhplus.ticketing.application.user.UserResult;
import org.hhplus.ticketing.utils.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
// @DirtiesContext 컨텍스트의 상태를 초기화
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserIntegrationTest {

    @Autowired
    private UserFacade userFacade;
    @Autowired
    TestDataInitializer testDataInitializer;

    private List<UserInfo> savedusers;

    private Long userId1;
    private Long userId2;
    private Long nonExistentUserId;

    @BeforeEach
    void setUp() {
        testDataInitializer.initializeTestData();

        // initializer 로 적재된 초기 데이터 세팅
        savedusers = testDataInitializer.getSavedUsers();

        userId1 = savedusers.get(0).getUserId();
        userId2 = savedusers.get(1).getUserId();
        nonExistentUserId = 99L;
    }

    @Test
    @DisplayName("🟢 [잔액_충전_통합_테스트]")
    void chargePointTest_유저1_5000포인트_충전시_5000포인트가_리턴된다() {

        // Given
        int addPoint = 5000;

        // 잔액 충전 요청 command 객체 생성
        UserCriteria.ChargePoint chargePoint = UserCriteria.ChargePoint.builder()
                .userId(userId1)
                .amount(addPoint)
                .build();

        // 예상 반환 result 객체 생성
        UserResult.ChargePoint expectedResult = new UserResult.ChargePoint(userId1, addPoint);

        // When
        UserResult.ChargePoint actualResult = userFacade.chargePoint(chargePoint);

        // Then
        assertNotNull(actualResult);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("🔴 [잔액_충전_통합_테스트]")
    void chargePointTest_유저정보가_없을_시_USER_NOT_FOUND_예외반환() {

        // Given
        int addPoint = 5000;

        UserCriteria.ChargePoint chargePoint99 = UserCriteria.ChargePoint.builder()
                .userId(nonExistentUserId)        // 99L : 유효하지 않은 사용자 ID
                .amount(addPoint)
                .build();

        // When & Then
        assertThatThrownBy(() -> userFacade.chargePoint(chargePoint99))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("🔴 [잔액_충전_통합_테스트]")
    void chargePointTest_포인트가_유효하지_않으면_INVALID_AMOUNT_VALUE_예외반환() {

        // Given
        int chargeAmount = 0;

        // 잔액 충전 요청 command 객체 생성
        UserCriteria.ChargePoint chargePoint = UserCriteria.ChargePoint.builder()
                .userId(userId1)
                .amount(chargeAmount)
                .build();

        // When & Then
        assertThatThrownBy(() -> userFacade.chargePoint(chargePoint))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_AMOUNT_VALUE);
    }

    @Test
    @DisplayName("🟢 [잔액_충전_통합_테스트]")
    void chargePointTest_기존에_포인트가_없는_유저2_3000포인트_충전시_3000포인트가_리턴된다() {

        // Given
        int addPoint = 3000;
        int oldPoint = 0;
        int finalPoint = oldPoint + addPoint;

        // 잔액 충전 요청 command 객체 생성
        UserCriteria.ChargePoint chargePoint = UserCriteria.ChargePoint.builder()
                .userId(userId2)
                .amount(addPoint)
                .build();

        // 예상 반환 result 객체 생성
        UserResult.ChargePoint expectedResult = new UserResult.ChargePoint(userId2, finalPoint);

        // When
        UserResult.ChargePoint actualResult = userFacade.chargePoint(chargePoint);

        // Then
        assertNotNull(actualResult);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("🟢 [잔액_조회_통합_테스트]")
    void getPointTest_유저1_포인트_조회시_1000포인트가_리턴된다() {

        // Given
        int oldPoint = 1000;

        // 초기 포인트 충전
        userFacade.chargePoint(new UserCriteria.ChargePoint(userId1, oldPoint));

        // 예상 반환 result 객체 생성
        UserResult.GetPoint expectedResult = new UserResult.GetPoint(userId1, oldPoint);

        // When
        UserResult.GetPoint actualResult = userFacade.getPointResult(userId1);

        // Then
        assertNotNull(actualResult);
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}