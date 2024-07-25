package org.hhplus.ticketing.interceptor;

import org.hhplus.ticketing.TicketingApplication;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.queue.QueueRepository;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.interfaces.controller.concert.dto.response.ConcertResponse;
import org.hhplus.ticketing.interfaces.controller.user.dto.UserResponse;
import org.hhplus.ticketing.support.exception.ErrorResponseEntity;
import org.hhplus.ticketing.utils.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = TicketingApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class InterceptorTest {

    @LocalServerPort
    private int port;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    TestDataInitializer testDataInitializer;

    private Long userId;
    private Long concertId;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    public void setup() {

        testDataInitializer.initializeTestData();

        userId = 1L;
        concertId = 1L;
    }

    @Test
    @DisplayName("🟢 인터셉터_토큰_검증_테스트_ACTIVE_토큰은_200상태코드를_응답한다")
    void validateTokenTest_인터셉터_토큰_검증_테스트_ACTIVE_토큰은_200상태코드를_응답한다() {

        // Given
        Queue savedQueue = queueRepository.save(Queue.builder()
                .userId(userId)
                .token(UUID.randomUUID())
                .status(Queue.Status.ACTIVE)
                .enteredAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .build());

        // HTTP 헤더에 인증 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + savedQueue.getToken());

        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // When
        // API 요청을 발송
        ResponseEntity<ConcertResponse.GetAvailableDatesResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/concerts/" + concertId + "/available-dates",
                HttpMethod.GET, entity, ConcertResponse.GetAvailableDatesResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("🔴 인터셉터_토큰_검증_테스트_WAITING_토큰은_INVALID_TOKEN_코드를_응답한다")
    void validateTokenTest_인터셉터_토큰_검증_테스트_WAITING_토큰은_INVALID_TOKEN_코드를_응답한다() {
        // Given
        Queue savedQueue = queueRepository.save(Queue.builder()
                .userId(userId)
                .token(UUID.randomUUID())
                .status(Queue.Status.WAITING)
                .createAt(LocalDateTime.now())
                .build());

        // HTTP 헤더에 인증 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + savedQueue.getToken());

        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // When
        // API 요청을 발송
        ResponseEntity<ErrorResponseEntity> response = restTemplate.exchange(
                getBaseUrl() + "/api/concerts/" + concertId + "/available-dates",
                HttpMethod.GET, entity, ErrorResponseEntity.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.INVALID_TOKEN.name());
    }

    @Test
    @DisplayName("🔴 인터셉터_토큰_검증_테스트_토큰이_존재하지_않을_경우_TOKEN_NOT_FOUND_코드를_응답한다")
    void validateTokenTest_인터셉터_토큰_검증_테스트_토큰이_존재하지_않을_경우_TOKEN_NOT_FOUND_코드를_응답한다() {
        // Given
        UUID invalidToken = UUID.randomUUID();

        // HTTP 헤더에 인증 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + invalidToken);

        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // When
        // API 요청을 발송
        ResponseEntity<ErrorResponseEntity> response = restTemplate.exchange(
                getBaseUrl() + "/api/concerts/" + concertId + "/available-dates",
                HttpMethod.GET, entity, ErrorResponseEntity.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.TOKEN_NOT_FOUND.name());
    }

    @Test
    @DisplayName("🔴 인터셉터_토큰_검증_테스트_헤더가_누락되었을경우_UNAUTHORIZED_상태를_응답한다")
    void validateTokenTest_인터셉터_토큰_검증_테스트_헤더가_누락되었을경우_UNAUTHORIZED_상태를_응답한다() {

        // Given
        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(null);

        // When
        // API 요청을 발송
        ResponseEntity<ErrorResponseEntity> response = restTemplate.exchange(
                getBaseUrl() + "/api/concerts/" + concertId + "/available-dates",
                HttpMethod.GET, entity, ErrorResponseEntity.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("헤더 정보가 누락되었습니다.");
    }

    @Test
    @DisplayName("🔴 인터셉터_토큰_검증_테스트_토큰_형식이_잘못되었을_경우_BAD_REQUEST_상태를_응답한다")
    void validateTokenTest_인터셉터_토큰_검증_테스트_토큰_형식이_잘못되었을_경우_BAD_REQUEST_상태를_응답한다() {

        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer invalid-token");

        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // When
        // API 요청을 발송
        ResponseEntity<ErrorResponseEntity> response = restTemplate.exchange(
                getBaseUrl() + "/api/concerts/" + concertId + "/available-dates",
                HttpMethod.GET, entity, ErrorResponseEntity.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("잘못된 토큰 형식입니다.");
    }

    @Test
    @DisplayName("🟢 인터셉터_토큰_검증_테스트_적용되지_않는_URL은_헤더에_영향받지_않는다")
    void validateTokenTest_인터셉터_토큰_검증_테스트_적용되지_않는_URL은_헤더에_영향받지_않는다() {

        // Given
        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(null);

        // When
        // API 요청을 발송
        ResponseEntity<UserResponse.UserPointResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/1/points",
                HttpMethod.GET, entity, UserResponse.UserPointResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
