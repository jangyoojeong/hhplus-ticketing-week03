package org.hhplus.ticketing.interfaces.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.application.user.facade.UserFacade;
import org.hhplus.ticketing.interfaces.controller.user.dto.UserRequest;
import org.hhplus.ticketing.interfaces.controller.user.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User API", description = "유저 관련 API")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserFacade userFacade;

    /**
     * 사용자의 잔액을 충전합니다.
     *
     * @param request 잔액 충전 요청 객체
     * @return 충전된 잔액 정보를 포함한 응답 객체
     */
    @PutMapping("/points/deposit")
    @Operation(summary = "잔액 충전 API", description = "사용자의 잔액을 충전합니다.")
    public ResponseEntity<UserResponse.AddPointResponse> addUserPoint(@Valid @RequestBody UserRequest.AddPointRequest request) {
        UserResponse.AddPointResponse response = UserResponse.AddPointResponse.from(userFacade.addUserPoint(request.toCommand()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 사용자의 잔액을 조회합니다.
     *
     * @param userId 잔액을 충전할 사용자의 ID
     * @return 잔액 응답 객체
     */
    @GetMapping("/{userId}/points")
    @Operation(summary = "잔액 조회 API", description = "사용자의 잔액을 조회합니다.")
    public ResponseEntity<UserResponse.UserPointResponse> getUserPoint (@PathVariable Long userId) {
        UserResponse.UserPointResponse response = UserResponse.UserPointResponse.from(userFacade.getUserPoint(userId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
