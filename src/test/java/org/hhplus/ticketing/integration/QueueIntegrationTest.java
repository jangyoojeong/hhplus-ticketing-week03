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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

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

    private List<UserInfo> savedusers;

    private Long userId;
    private Long nonExistentUserId;

    @BeforeEach
    void setUp() {
        testDataInitializer.initializeTestData();

        // initializer 로 적재된 초기 데이터 세팅
        savedusers = testDataInitializer.getSavedUsers();

        userId = savedusers.get(0).getUserId();
        nonExistentUserId = 99L;
    }

    @Test
    @DisplayName("🟢 토큰_발급_통합_테스트_토큰_슬롯_남아있을_경우_ACTIVE_상태로_발급된다")
    void issueTokenTest_토큰_발급_통합_테스트_토큰_슬롯_남아있을_경우_ACTIVE_상태로_발급된다() {
        // Given
        QueueCommand.IssueTokenCommand command = new QueueCommand.IssueTokenCommand(userId);

        // When
        QueueResult.IssueTokenResult actualTokenResult = queueFacade.issueToken(command);

        // Then
        Optional<Queue> queue = queueRepository.findByToken(actualTokenResult.getToken());
        assertNotNull(actualTokenResult);
        assertEquals(Queue.Status.ACTIVE, queue.get().getStatus());
    }

    @Test
    @DisplayName("🟢 토큰_발급_통합_테스트_토큰_슬롯_한도_초과시_WAITING_상태로_발급된다")
    void issueTokenTest_토큰_발급_통합_테스트_토큰_슬롯_한도_초과시_WAITING_상태로_발급된다() {
        // Given
        // 모든 활성화 슬롯 채우기
        int maxActiveUsers = QueueConstants.MAX_ACTIVE_USERS;
        for (int i = 0; i < maxActiveUsers; i++) {
            Queue activeQueue = Queue.create((long) i, (long) i);
            queueRepository.save(activeQueue);
        }

        QueueCommand.IssueTokenCommand command = new QueueCommand.IssueTokenCommand(userId);

        // When
        QueueResult.IssueTokenResult actualTokenResult = queueFacade.issueToken(command);

        // Then
        Optional<Queue> queue = queueRepository.findByToken(actualTokenResult.getToken());
        assertNotNull(actualTokenResult);
        assertEquals(Queue.Status.WAITING, queue.get().getStatus());
    }

    @Test
    @DisplayName("🔴 토큰_발급_통합_테스트_유저정보가_없을_시_USER_NOT_FOUND_예외반환")
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
    @DisplayName("🟢 대기열_상태_조회_통합_테스트_첫번째_발급된_대기열_토큰의_대기순서는_0을_리턴한다")
    void getQueueStatusTest_대기열_상태_조회_통합_테스트_첫번째_발급된_대기열_토큰의_대기순서는_0을_리턴한다() {
        // Given
        QueueCommand.IssueTokenCommand command = new QueueCommand.IssueTokenCommand(userId);
        QueueResult.IssueTokenResult tokenResult = queueFacade.issueToken(command);
        UUID issuedToken = tokenResult.getToken();

        // When
        QueueResult.QueueStatusResult actualStatusResult = queueFacade.getQueueStatus(issuedToken);

        // Then
        assertNotNull(actualStatusResult);
        assertEquals(0, actualStatusResult.getPosition());
    }

    @Test
    @DisplayName("🟢 대기열_상태_조회_통합_테스트_30번째_발급된_대기열_토큰의_대기순서는_10을_리턴한다")
    void getQueueStatusTest_대기열_상태_조회_통합_테스트_30번째_발급된_대기열_토큰의_대기순서는_10을_리턴한다() {
        // Given
        // 모든 활성화 슬롯 채우기
        int maxActiveUsers = QueueConstants.MAX_ACTIVE_USERS;
        for (int i = 0; i < maxActiveUsers; i++) {
            Queue activeQueue = Queue.create((long) i, (long) i);
            queueRepository.save(activeQueue);
        }

        // 대기열 채우기 (9명)
        for (int i = 0; i < 9; i++) {
            Queue watingQueue = Queue.create((long) maxActiveUsers, (long) i);
            queueRepository.save(watingQueue);
        }

        // 10번째 대기열 토큰 발급
        QueueCommand.IssueTokenCommand command = new QueueCommand.IssueTokenCommand(userId);
        QueueResult.IssueTokenResult tokenResult = queueFacade.issueToken(command);
        UUID issuedToken = tokenResult.getToken();

        // When
        QueueResult.QueueStatusResult actualStatusResult = queueFacade.getQueueStatus(issuedToken);

        // Then
        assertNotNull(actualStatusResult);
        assertEquals(10, actualStatusResult.getPosition());
    }

    @Test
    @DisplayName("🔴 대기열_토큰_검증_테스트_WAITING_토큰_INVALID_TOKEN_예외반환")
    void validateTokenTest_대기열_토큰_검증_테스트_유효하지_않은_토큰_INVALID_TOKEN_예외반환() {
        // Given
        Queue queue = Queue.builder()
                .userId(userId)
                .status(Queue.Status.WAITING)
                .build();

        Queue savedQueue = queueRepository.save(queue);

        // When & Then
        assertThatThrownBy(() -> queueFacade.validateToken(savedQueue.getToken()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("🟢 대기열_상태_업데이트_테스트_총_2건중_만료대상토큰_1건만_만료된다")
    void updateQueueStatusesTest_대기열_상태_업데이트_테스트_총_2건중_만료대상토큰_1건만_만료된다() {

        // Given
        // 활성화토큰1 (만료대상토큰)
        Queue activeQueue1 = Queue.create(1L, 1L);
        activeQueue1.setEnteredAt(LocalDateTime.now().minusMinutes(QueueConstants.TOKEN_EXPIRATION_MINUTES - 1));
        queueRepository.save(activeQueue1);

        // 활성화토큰2 (만료대상이 아닌 토큰)
        Queue activeQueue2 = Queue.create(2L, 2L);
        activeQueue2.setEnteredAt(LocalDateTime.now().minusMinutes(QueueConstants.TOKEN_EXPIRATION_MINUTES + 1));
        queueRepository.save(activeQueue2);

        // When
        queueFacade.refreshQueue();

        // Then
        Long expiredQueueCnt = queueRepository.countByStatus(Queue.Status.EXPIRED);
        Long activeQueueCnt = queueRepository.countByStatus(Queue.Status.ACTIVE);

        assertEquals(1, expiredQueueCnt);
        assertEquals(1, activeQueueCnt);
    }

    @Test
    @DisplayName("🟢 대기_중인_토큰_활성화_상태로_변경_테스트_슬롯이_있으면_활성화된다")
    void activateWaitingTokensTest_대기_중인_토큰_활성화_상태로_변경_테스트_슬롯이_있으면_활성화된다() {

        // Given
        // 활성화토큰1
        Queue activeQueue1 = Queue.builder()
                .userId(3L)
                .token(UUID.randomUUID())
                .status(Queue.Status.ACTIVE)
                .enteredAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .build();
        queueRepository.save(activeQueue1);

        // 대기토큰1 (대기순서1)
        Queue waitingQueue1 = Queue.builder()
                .userId(4L)
                .token(UUID.randomUUID())
                .status(Queue.Status.WAITING)
                .createAt(LocalDateTime.now().minusMinutes(10))
                .build();
        queueRepository.save(waitingQueue1);

        // 대기토큰2 (대기순서2)
        Queue waitingQueue2 = Queue.builder()
                .userId(5L)
                .token(UUID.randomUUID())
                .status(Queue.Status.WAITING)
                .createAt(LocalDateTime.now().minusMinutes(5))
                .build();
        queueRepository.save(waitingQueue2);

        // When
        queueFacade.refreshQueue();

        // Then
        Long activeQueueCnt = queueRepository.countByStatus(Queue.Status.ACTIVE);
        Long waitingQueueCnt = queueRepository.countByStatus(Queue.Status.WAITING);

        // 한 개의 활성화된 토큰에서 두 개가 더 활성화됨
        assertEquals(3, activeQueueCnt);
        // 모든 대기 중인 토큰이 활성화됨
        assertEquals(0, waitingQueueCnt);
    }

    @Test
    @DisplayName("🟢 대기_중인_토큰_활성화_상태로_변경_테스트_슬롯이_없으면_활성화되지_않는다")
    void activateWaitingTokensTest_대기_중인_토큰_활성화_상태로_변경_테스트_슬롯이_없으면_활성화되지_않는다() {
        // Given
        // 모든 활성화 슬롯을 채워서 대기 중인 토큰이 활성화될 수 없도록 설정
        int maxActiveUsers = QueueConstants.MAX_ACTIVE_USERS;
        for (int i = 0; i < maxActiveUsers; i++) {
            Queue activeQueue = Queue.create((long) i, (long) i);
            queueRepository.save(activeQueue);
        }

        // 대기토큰1 (대기순서1)
        Queue waitingQueue1 = Queue.builder()
                .userId(4L)
                .token(UUID.randomUUID())
                .status(Queue.Status.WAITING)
                .createAt(LocalDateTime.now().minusMinutes(10))
                .build();
        queueRepository.save(waitingQueue1);

        // When
        queueFacade.refreshQueue();

        // Then
        Long activeQueueCnt = queueRepository.countByStatus(Queue.Status.ACTIVE);
        Long waitingQueueCnt = queueRepository.countByStatus(Queue.Status.WAITING);

        // 최종 활성화토큰이 MAX_ACTIVE_USERS와 일치하는지 확인
        assertEquals(maxActiveUsers, activeQueueCnt);
        // 추가로 활성화 되지 않고 대기토큰이 존재하는지 확인
        assertEquals(1, waitingQueueCnt);
    }
}
