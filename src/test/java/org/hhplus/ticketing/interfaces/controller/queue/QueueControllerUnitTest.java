package org.hhplus.ticketing.interfaces.controller.queue;

import org.hhplus.ticketing.application.queue.QueueFacade;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
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
    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        token = UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("🟢 대기열_토큰_발급_컨트롤러_테스트_헤더_토큰정보_리턴_확인")
    void issueTokenTest_대기열_토큰_발급_컨트롤러_테스트_헤더_토큰정보_리턴_확인 () throws Exception {
        // Given
        QueueRequest.IssueToken request = new QueueRequest.IssueToken(userId);
        QueueResult.IssueToken result = new QueueResult.IssueToken(token, 1L, "00시간 00분");
        QueueResponse.IssueTokenResponse response = QueueResponse.IssueTokenResponse.from(result);

        given(queueFacade.issueToken(any(QueueCommand.IssueToken.class))).willReturn(result);

        // When
        ResponseEntity<QueueResponse.IssueTokenResponse> responseEntity = queueController.issueToken(request);

        // Then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        assertEquals("Bearer " + token, responseEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    @DisplayName("🟢 대기열_확인_컨트롤러_테스트_예상_리턴_데이터_확인")
    void getQueueStatusTest_대기열_확인_컨트롤러_테스트_예상_리턴_데이터_확인 () throws Exception {
        // Given
        QueueResult.QueueStatus result = new QueueResult.QueueStatus(1L, "00시간 00분");
        QueueResponse.QueueStatusResponse response = QueueResponse.QueueStatusResponse.from(result);

        given(queueFacade.getQueueStatus(any(String.class))).willReturn(result);

        // When
        ResponseEntity<QueueResponse.QueueStatusResponse> responseEntity = queueController.getQueueStatus("Bearer " + token);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

}