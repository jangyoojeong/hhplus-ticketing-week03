package org.hhplus.ticketing.integration;

import org.hhplus.ticketing.application.user.facade.UserFacade;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.user.UserPointRepository;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserInfoDomain;
import org.hhplus.ticketing.domain.user.model.UserPointDomain;
import org.hhplus.ticketing.domain.user.model.UserResult;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
// @DirtiesContext 컨텍스트의 상태를 초기화
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserIntegrationTest {

    @Autowired
    private UserFacade userFacade;
    @Autowired
    private UserPointRepository userPointRepository;
    @Autowired
    TestDataInitializer testDataInitializer;

    private List<UserInfoDomain> savedusers;

    private Long userId1;
    private Long userId2;
    private Long nonExistentUserId;

    @BeforeEach
    void setUp() {
        testDataInitializer.initializeTestData();

        // initializer 로 적재된 초기 데이터 세팅
        savedusers = testDataInitializer.getSavedusers();

        userId1 = savedusers.get(0).getUserId();
        userId2 = savedusers.get(1).getUserId();
        nonExistentUserId = 99L;
    }

    @Test
    @DisplayName("[실패테스트] 잔액_충전_통합_테스트_유저정보가_없을_시_예외_발생")
    void addUserPointTest_잔액_충전_통합_테스트_유저정보가_없을_시_예외_발생() {

        // Given
        int addPoint = 5000;

        UserCommand.AddPointCommand addPointCommand99 = UserCommand.AddPointCommand.builder()
                .userId(nonExistentUserId)        // 99L : 유효하지 않은 사용자 ID
                .amount(addPoint)
                .build();

        // When & Then
        assertThatThrownBy(() -> userFacade.addUserPoint(addPointCommand99))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("[성공테스트] 잔액_충전_통합_테스트_유저1_5000포인트_충전시_6000포인트가_리턴된다")
    void addUserPointTest_잔액_충전_통합_테스트_유저1_5000포인트_충전시_5000포인트가_리턴된다() {

        // Given
        int addPoint = 5000;
        int oldPoint = 1000;
        int finalPoint = oldPoint + addPoint;

        // 초기 1000 포인트 적재
        UserPointDomain userPointDomain = UserPointDomain.builder()
                .userId(userId1)
                .point(oldPoint)
                .build();

        userPointRepository.save(userPointDomain);

        // 잔액 충전 요청 command 객체 생성
        UserCommand.AddPointCommand addPointCommand = UserCommand.AddPointCommand.builder()
                .userId(userId1)
                .amount(addPoint)
                .build();

        // 예상 반환 result 객체 생성
        UserResult.AddPointResult expectedResult = new UserResult.AddPointResult(userId1, finalPoint);

        // When
        UserResult.AddPointResult actualResult = userFacade.addUserPoint(addPointCommand);

        // Then
        assertNotNull(actualResult);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("[성공테스트] 잔액_충전_통합_테스트_기존에_포인트가_없는_유저2_3000포인트_충전시_3000포인트가_리턴된다")
    void addUserPointTest_잔액_충전_통합_테스트_기존에_포인트가_없는_유저2_3000포인트_충전시_3000포인트가_리턴된다() {

        // Given
        int addPoint = 3000;
        int oldPoint = 0;
        int finalPoint = oldPoint + addPoint;

        // 잔액 충전 요청 command 객체 생성
        UserCommand.AddPointCommand addPointCommand = UserCommand.AddPointCommand.builder()
                .userId(userId2)
                .amount(addPoint)
                .build();

        // 예상 반환 result 객체 생성
        UserResult.AddPointResult expectedResult = new UserResult.AddPointResult(userId2, finalPoint);

        // When
        UserResult.AddPointResult actualResult = userFacade.addUserPoint(addPointCommand);

        // Then
        assertNotNull(actualResult);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("[성공테스트] 잔액_조회_통합_테스트_유저1_포인트_조회시_1000포인트가_리턴된다")
    void getUserPointTest_잔액_조회_통합_테스트_유저1_포인트_조회시_1000포인트가_리턴된다() {

        // Given
        int oldPoint = 1000;

        // 초기 1000 포인트 적재
        UserPointDomain userPointDomain = UserPointDomain.builder()
                .userId(userId1)
                .point(oldPoint)
                .build();

        userPointRepository.save(userPointDomain);

        // 예상 반환 result 객체 생성
        UserResult.UserPointResult expectedResult = new UserResult.UserPointResult(userId1, oldPoint);

        // When
        UserResult.UserPointResult actualResult = userFacade.getUserPoint(userId1);

        // Then
        assertNotNull(actualResult);
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}