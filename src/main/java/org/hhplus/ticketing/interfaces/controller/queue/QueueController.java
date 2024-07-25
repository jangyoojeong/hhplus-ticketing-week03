package org.hhplus.ticketing.interfaces.controller.queue;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.application.queue.QueueFacade;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.interfaces.controller.queue.dto.request.QueueRequest;
import org.hhplus.ticketing.interfaces.controller.queue.dto.response.QueueResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 대기열 관련 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/queues")
@Tag(name = "Queue API", description = "대기열 관련 API")
public class QueueController {

    private final QueueFacade queueFacade;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 콘서트 대기열에 입장할 때 사용하는 토큰을 발급합니다.
     * 사용자는 이 토큰을 통해 대기열에 대한 인증을 받을 수 있습니다.
     *
     * @param request 토큰 발급 요청 객체
     * @return 발급된 토큰과 대기열 정보를 포함한 응답 객체
     */
    @PostMapping("/token")
    @Operation(summary = "토큰 발급 API", description = "콘서트 대기열에 입장할 때 사용하는 토큰을 발급합니다.")
    public ResponseEntity<QueueResponse.IssueTokenResponse> issueToken (@RequestBody QueueRequest.IssueTokenRequest request) {
        QueueResult.IssueTokenResult queueResult = queueFacade.issueToken(request.toCommand());
        QueueResponse.IssueTokenResponse response = QueueResponse.IssueTokenResponse.from(queueResult);

        // 발급된 토큰 헤더에 리턴
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, BEARER_PREFIX + queueResult.getToken().toString());

        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    /**
     * 사용자의 대기열 상태(대기순번 등)를 반환합니다.
     *
     * @param authorizationHeader 인증 토큰을 포함한 헤더
     * @return 대기순번 등 대기열 상태 응답 객체
     */
    @GetMapping("/status")
    @Operation(summary = "대기열 조회 API", description = "사용자의 대기열 상태(대기순번 등)를 조회합니다.")
    public ResponseEntity<QueueResponse.QueueStatusResponse> getQueueStatus (@RequestHeader(value = AUTHORIZATION_HEADER, required = true) String authorizationHeader) {
        String token = authorizationHeader.replace(BEARER_PREFIX, "");
        QueueResponse.QueueStatusResponse response = QueueResponse.QueueStatusResponse.from(queueFacade.getQueueStatus(UUID.fromString(token)));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
