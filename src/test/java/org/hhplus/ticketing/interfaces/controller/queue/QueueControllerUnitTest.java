package org.hhplus.ticketing.interfaces.controller.queue;

import org.hhplus.ticketing.application.queue.facade.QueueFacade;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.queue.model.enums.TokenStatus;
import org.hhplus.ticketing.interfaces.controller.queue.dto.request.QueueRequest;
import org.hhplus.ticketing.interfaces.controller.queue.dto.response.QueueResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

// 대기열 컨트롤러 단위테스트입니다.
public class QueueControllerUnitTest {
    @InjectMocks
    private QueueController queueController;

    @Mock
    private QueueFacade queueFacade;

    private Long userId;
    private UUID token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        token = UUID.randomUUID();
    }

    @Test
    @DisplayName("[성공테스트] 대기열_토큰_발급_컨트롤러_테스트_헤더_토큰정보_리턴_확인")
    void issueTokenTest_대기열_토큰_발급_컨트롤러_테스트_헤더_토큰정보_리턴_확인 () throws Exception {
        // Given
        QueueRequest.IssueTokenRequest request = new QueueRequest.IssueTokenRequest(userId);
        QueueResult.IssueTokenResult result = new QueueResult.IssueTokenResult(userId, token);
        QueueResponse.IssueTokenResponse response = QueueResponse.IssueTokenResponse.from(result);

        given(queueFacade.issueToken(any(QueueCommand.IssueTokenCommand.class))).willReturn(result);

        // When
        ResponseEntity<QueueResponse.IssueTokenResponse> responseEntity = queueController.issueToken(request);

        // Then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        assertEquals("Bearer " + token.toString(), responseEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    @DisplayName("[성공테스트] 대기열_확인_컨트롤러_테스트_예상_리턴_데이터_확인")
    void getQueueStatusTest_대기열_확인_컨트롤러_테스트_예상_리턴_데이터_확인 () throws Exception {
        // Given
        QueueResult.QueueStatusResult result = new QueueResult.QueueStatusResult(userId, token, 0L, TokenStatus.ACTIVE);
        QueueResponse.QueueStatusResponse response = QueueResponse.QueueStatusResponse.from(result);

        given(queueFacade.getQueueStatus(any(UUID.class))).willReturn(result);

        // When
        ResponseEntity<QueueResponse.QueueStatusResponse> responseEntity = queueController.getQueueStatus("Bearer " + token.toString());

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

}