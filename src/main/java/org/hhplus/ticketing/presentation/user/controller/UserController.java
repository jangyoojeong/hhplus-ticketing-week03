package org.hhplus.ticketing.presentation.user.controller;

import org.hhplus.ticketing.presentation.user.dto.request.AddPointRequest;
import org.hhplus.ticketing.presentation.user.dto.response.AddPointResponse;
import org.hhplus.ticketing.presentation.user.dto.response.UserPointResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * 사용자의 잔액을 충전합니다.
     *
     * @param request 잔액 충전 요청 객체
     * @return 충전된 잔액 정보를 포함한 응답 객체
     */
    @PostMapping("/deposit")
    public ResponseEntity<AddPointResponse> addPoint (@RequestBody AddPointRequest request) {
        int point = 50000;  // 기존포인트 (가정)
        AddPointResponse response = new AddPointResponse(request.getUuid(), request.getAmount() + point);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자의 잔액을 조회합니다.
     *
     * @param uuid 잔액을 충전할 사용자의 고유 UUID
     * @return 예약 가능한 좌석 목록을 포함한 응답 객체
     */
    @GetMapping("/{uuid}/balance")
    public ResponseEntity<UserPointResponse> getUserPoint (@PathVariable String uuid) {
        UserPointResponse response = new UserPointResponse(uuid, 100000);
        return ResponseEntity.ok(response);
    }
}
