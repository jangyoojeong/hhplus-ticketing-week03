package org.hhplus.ticketing.integration;

import org.hhplus.ticketing.application.queue.facade.QueueFacade;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.queue.QueueRepository;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;
import org.hhplus.ticketing.domain.queue.model.QueueDomain;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;
import org.hhplus.ticketing.domain.queue.model.enums.TokenStatus;
import org.hhplus.ticketing.domain.user.model.UserInfoDomain;
import org.hhplus.ticketing.utils.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    private List<UserInfoDomain> savedusers;

    private Long userId;
    private Long nonExistentUserId;

    @BeforeEach
    void setUp() {
        testDataInitializer.initializeTestData();

        // initializer 로 적재된 초기 데이터 세팅
        savedusers = testDataInitializer.getSavedusers();

        userId = savedusers.get(0).getUserId();
        nonExistentUserId = 99L;
    }

    @Test
    @DisplayName("[성공테스트] 토큰_발급_통합_테스트_토큰_슬롯_남아있을_경우_ACTIVE_상태로_발급된다")
    void issueTokenTest_토큰_발급_통합_테스트_토큰_슬롯_남아있을_경우_ACTIVE_상태로_발급된다() {
        // Given
        QueueCommand.IssueTokenCommand command = new QueueCommand.IssueTokenCommand(userId);

        // When
        QueueResult.IssueTokenResult actualTokenResult = queueFacade.issueToken(command);

        // Then
        Optional<QueueDomain> queueDomain = queueRepository.findByToken(actualTokenResult.getToken());
        assertNotNull(actualTokenResult);
        assertEquals(TokenStatus.ACTIVE, queueDomain.get().getStatus());
    }

    @Test
    @DisplayName("[성공테스트] 토큰_발급_통합_테스트_토큰_슬롯_한도_초과시_WAITING_상태로_발급된다")
    void issueTokenTest_토큰_발급_통합_테스트_토큰_슬롯_한도_초과시_WAITING_상태로_발급된다() {
        // Given
        // 모든 활성화 슬롯 채우기
        int maxActiveUsers = QueueConstants.MAX_ACTIVE_USERS;
        for (int i = 0; i < maxActiveUsers; i++) {
            QueueDomain activeQueue = QueueDomain.createActiveQueue((long) i);
            queueRepository.save(activeQueue);
        }

        QueueCommand.IssueTokenCommand command = new QueueCommand.IssueTokenCommand(userId);

        // When
        QueueResult.IssueTokenResult actualTokenResult = queueFacade.issueToken(command);

        // Then
        Optional<QueueDomain> queueDomain = queueRepository.findByToken(actualTokenResult.getToken());
        assertNotNull(actualTokenResult);
        assertEquals(TokenStatus.WAITING, queueDomain.get().getStatus());
    }

    @Test
    @DisplayName("[실패테스트] 토큰_발급_통합_테스트_유저정보가_없을_시_USER_NOT_FOUND_예외반환")
    void issueTokenTest_토큰_발급_통합_테스트_유저정보가_없을_시_예외_발생() {
        // Given
        QueueCommand.IssueTokenCommand command = new QueueCommand.IssueTokenCommand(nonExistentUserId);

        // When & Then
        assertThatThrownBy(() -> queueFacade.issueToken(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("[성공테스트] 대기열_상태_조회_통합_테스트_첫번째_발급된_대기열_토큰의_대기순서는_1을_리턴한다")
    void getQueueStatusTest_대기열_상태_조회_통합_테스트_첫번째_발급된_대기열_토큰의_대기순서는_1을_리턴한다() {
        // Given
        QueueCommand.IssueTokenCommand command = new QueueCommand.IssueTokenCommand(userId);
        QueueResult.IssueTokenResult tokenResult = queueFacade.issueToken(command);
        UUID issuedToken = tokenResult.getToken();

        // When
        QueueResult.QueueStatusResult actualStatusResult = queueFacade.getQueueStatus(issuedToken);

        // Then
        assertNotNull(actualStatusResult);
        assertEquals(1, actualStatusResult.getQueuePosition());
    }

    @Test
    @DisplayName("[실패테스트] 대기열_토큰_검증_테스트_유효하지_않은_토큰_INVALID_TOKEN_예외반환")
    void validateTokenTest_대기열_토큰_검증_테스트_유효하지_않은_토큰_INVALID_TOKEN_예외반환() {
        // Given
        QueueDomain queueDomain = QueueDomain.builder()
                .userId(userId)
                .status(TokenStatus.WAITING)
                .build();

        QueueDomain savedQueue = queueRepository.save(queueDomain);

        // When & Then
        assertThatThrownBy(() -> queueFacade.validateToken(savedQueue.getToken()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("[성공테스트] 대기열_상태_업데이트_테스트_총_2건중_만료대상토큰_1건만_만료된다")
    void updateQueueStatusesTest_대기열_상태_업데이트_테스트_총_2건중_만료대상토큰_1건만_만료된다() {

        // Given
        // 활성화토큰1 (만료대상토큰)
        QueueDomain activeQueue1 = QueueDomain.createActiveQueue(1L);
        activeQueue1.setEnteredAt(LocalDateTime.now().minusMinutes(QueueConstants.TOKEN_EXPIRATION_TIME_MINUTES - 1));
        queueRepository.save(activeQueue1);

        // 활성화토큰2 (만료대상이 아닌 토큰)
        QueueDomain activeQueue2 = QueueDomain.createActiveQueue(2L);
        activeQueue2.setEnteredAt(LocalDateTime.now().minusMinutes(QueueConstants.TOKEN_EXPIRATION_TIME_MINUTES + 1));
        queueRepository.save(activeQueue2);

        // When
        queueFacade.updateQueueStatuses();

        // Then
        Long expiredQueueCnt = queueRepository.countByStatus(TokenStatus.EXPIRED);
        Long activeQueueCnt = queueRepository.countByStatus(TokenStatus.ACTIVE);

        assertEquals(1, expiredQueueCnt);
        assertEquals(1, activeQueueCnt);
    }

    @Test
    @DisplayName("[성공테스트] 대기_중인_토큰_활성화_상태로_변경_테스트_슬롯이_있으면_활성화된다")
    void activateWaitingTokensTest_대기_중인_토큰_활성화_상태로_변경_테스트_슬롯이_있으면_활성화된다() {

        // Given
        // 활성화토큰1
        QueueDomain activeQueue1 = QueueDomain.createActiveQueue(3L);
        queueRepository.save(activeQueue1);

        // 대기토큰1 (대기순서1)
        QueueDomain waitingQueue1 = QueueDomain.createWaitingQueue(4L);
        waitingQueue1.setCreateAt(LocalDateTime.now().minusMinutes(10));
        queueRepository.save(waitingQueue1);

        // 대기토큰2 (대기순서2)
        QueueDomain waitingQueue2 = QueueDomain.createWaitingQueue(5L);
        waitingQueue2.setCreateAt(LocalDateTime.now().minusMinutes(5));
        queueRepository.save(waitingQueue2);

        // When
        queueFacade.updateQueueStatuses();

        // Then
        Long activeQueueCnt = queueRepository.countByStatus(TokenStatus.ACTIVE);
        Long waitingQueueCnt = queueRepository.countByStatus(TokenStatus.WAITING);

        // 한 개의 활성화된 토큰에서 두 개가 더 활성화됨
        assertEquals(3, activeQueueCnt);
        // 모든 대기 중인 토큰이 활성화됨
        assertEquals(0, waitingQueueCnt);
    }

    @Test
    @DisplayName("[성공테스트] 대기_중인_토큰_활성화_상태로_변경_테스트_슬롯이_없으면_활성화되지_않는다")
    @Transactional
    void activateWaitingTokensTest_대기_중인_토큰_활성화_상태로_변경_테스트_슬롯이_없으면_활성화되지_않는다() {
        // Given
        // 모든 활성화 슬롯을 채워서 대기 중인 토큰이 활성화될 수 없도록 설정
        int maxActiveUsers = QueueConstants.MAX_ACTIVE_USERS;
        for (int i = 0; i < maxActiveUsers; i++) {
            QueueDomain activeQueue = QueueDomain.createActiveQueue((long) (i + 4));
            queueRepository.save(activeQueue);
        }

        // 대기토큰1 (대기순서1)
        QueueDomain waitingQueue1 = QueueDomain.createWaitingQueue(4L);
        waitingQueue1.setCreateAt(LocalDateTime.now().minusMinutes(10));
        queueRepository.save(waitingQueue1);

        // When
        queueFacade.updateQueueStatuses();

        // Then
        Long activeQueueCnt = queueRepository.countByStatus(TokenStatus.ACTIVE);
        Long waitingQueueCnt = queueRepository.countByStatus(TokenStatus.WAITING);

        // 최종 활성화토큰이 MAX_ACTIVE_USERS와 일치하는지 확인
        assertEquals(maxActiveUsers, activeQueueCnt);
        // 추가로 활성화 되지 않고 대기토큰이 존재하는지 확인
        assertEquals(1, waitingQueueCnt);
    }
}
