package org.hhplus.ticketing.presentation.queue.controller;

import org.hhplus.ticketing.presentation.queue.dto.request.IssueTokenRequest;
import org.hhplus.ticketing.presentation.queue.dto.response.IssueTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/queues")
public class QueueController {

    private static final Logger log = LoggerFactory.getLogger(QueueController.class);

    /**
     * 콘서트 대기열에 입장할 때 사용하는 토큰을 발급합니다.
     * 사용자는 이 토큰을 통해 대기열에 대한 인증을 받을 수 있습니다.
     *
     * @param request 토큰 발급 요청 객체
     * @return 발급된 토큰과 대기열 정보를 포함한 응답 객체
     */
    @PostMapping("/token")
    public ResponseEntity<IssueTokenResponse> issueToken (@RequestBody IssueTokenRequest request) {
        IssueTokenResponse response = new IssueTokenResponse(request.getUuid(), "123e4567-e89b-12d3-a456-426614174000-5000", 5000);
        return ResponseEntity.ok(response);
    }

}
