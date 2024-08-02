package org.hhplus.ticketing.integration;

import org.hhplus.ticketing.application.queue.QueueFacade;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.queue.QueueRepository;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;
import org.hhplus.ticketing.domain.user.model.UserInfo;
import org.hhplus.ticketing.utils.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
// @DirtiesContext 컨텍스트의 상태를 초기화
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class QueueIntegrationTest {

    @Autowired
    private QueueFacade queueFacade;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    TestDataInitializer testDataInitializer;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private List<UserInfo> savedusers;

    private Long userId;
    private Long nonExistentUserId;

    @BeforeEach
    void setUp() {
        // 모든 키 삭제
        redisTemplate.getConnectionFactory().getConnection().flushDb();

        testDataInitializer.initializeTestData();

        // initializer 로 적재된 초기 데이터 세팅
        savedusers = testDataInitializer.getSavedUsers();

        userId = savedusers.get(0).getUserId();
        nonExistentUserId = 99L;
    }

    @Test
    @DisplayName("🟢 토큰_발급_통합_테스트_바로_입장_가능할경우_ACTIVE_토큰이_발급되고_대기순번_null을_리턴한다")
    void issueTokenTest_토큰_발급_통합_테스트_바로_입장_가능할경우_ACTIVE_토큰이_발급되고_대기순번_null을_리턴한다() {
        // Given
        QueueCommand.IssueToken command = new QueueCommand.IssueToken(userId);

        // When
        QueueResult.IssueToken actualResult = queueFacade.issueToken(command);

        // Then
        assertNotNull(actualResult);
        assertNull(actualResult.getPosition());
        assertEquals(actualResult.getStatus(), Queue.Status.ACTIVE);
    }

    @Test
    @DisplayName("🔴 토큰_발급_통합_테스트_유저정보가_없을_시_USER_NOT_FOUND_예외반환")
    void issueTokenTest_토큰_발급_통합_테스트_유저정보가_없을_시_예외_발생() {
        // Given
        QueueCommand.IssueToken command = new QueueCommand.IssueToken(nonExistentUserId);

        // When & Then
        assertThatThrownBy(() -> queueFacade.issueToken(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("🟢 대기열_상태_조회_통합_테스트_첫번째_발급된_활성화_토큰_순서는_0L을_리턴한다.")
    void getQueueStatusTest_대기열_상태_조회_통합_테스트_첫번째_발급된_활성화_토큰_순서는_0L을_리턴한다() {
        // Given
        QueueResult.IssueToken tokenResult = queueFacade.issueToken(new QueueCommand.IssueToken(userId));
        String issuedToken = tokenResult.getToken();

        // When
        QueueResult.QueueStatus actualResult = queueFacade.getQueueStatus(issuedToken);

        // Then
        assertEquals(0, actualResult.getPosition());
    }

    @Test
    @DisplayName("🟢 대기열_상태_조회_통합_테스트_20번째_발급된_대기열_토큰의_대기순서는_20을_리턴한다")
    void getQueueStatusTest_대기열_상태_조회_통합_테스트_20번째_발급된_대기열_토큰의_대기순서는_20을_리턴한다() {
        // Given
        // 모든 대기열 슬롯 채우기
        for (int i = 0; i < 19; i++) {
            Queue queue = Queue.create();
            queueRepository.addWaiting(queue);
        }

        // 20번째 대기열 토큰 발급
        Queue queue = Queue.create();
        String issuedToken = queue.getToken();
        queueRepository.addWaiting(queue);

        // When
        QueueResult.QueueStatus actualStatusResult = queueFacade.getQueueStatus(issuedToken);

        // Then
        assertNotNull(actualStatusResult);
        assertEquals(20, actualStatusResult.getPosition());
    }

    @Test
    @DisplayName("🔴 토큰_검증_테스트_유효하지_않은_토큰일경우_INVALID_TOKEN_예외반환")
    void validateTokenTest_토큰_검증_테스트_유효하지_않은_토큰일경우_INVALID_TOKEN_예외반환() {
        // Given
        Queue queue = Queue.create();
        String token = queue.getToken();
        queueRepository.addWaiting(queue);

        // When & Then
        assertThatThrownBy(() -> queueFacade.validateToken(token))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("🟢 대기열_상태_업데이트_테스트_WAITING_토큰_중_MAX_ACTIVE_TOKENS_개수만_활성화된다")
    void activateTest_대기열_상태_업데이트_테스트_WAITING_토큰_중_MAX_ACTIVE_TOKENS_개수만_활성화된다() {

        // Given
        for (int i = 0; i < QueueConstants.MAX_ACTIVE_TOKENS + 5; i++) {
            Queue queue = Queue.create();
            queueRepository.addWaiting(queue);
        }

        // When
        queueFacade.activate();

        // Then
        Long count = queueRepository.countActiveTokens();
        assertThat(count).isEqualTo(QueueConstants.MAX_ACTIVE_TOKENS);
    }

    @Test
    @DisplayName("🟢 대기열_상태_업데이트_테스트_WAITING_토큰이_없으면_활성화되지_않는다")
    void activateTest_대기열_상태_업데이트_테스트_WAITING_토큰이_없으면_활성화되지_않는다() {

        // When
        queueFacade.activate();

        // Then
        long count = queueRepository.countActiveTokens();
        assertThat(count).isEqualTo(0);
    }
}
