package org.hhplus.ticketing.application.user;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.user.UserInfoService;
import org.hhplus.ticketing.domain.user.UserPointService;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

/**
 * 유저관련 비지니스 로직을 캡슐화하는 파사드 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserPointService userPointService;
    private final UserInfoService userInfoService;

    /**
     * 사용자의 잔액을 조회합니다.
     *
     * @param userId 잔액을 조회할 사용자의 ID
     * @return 잔액 result 객체
     */
    public UserResult.UserPointResult getPointResult(Long userId) {
        return userPointService.getPointResult(userId);
    }

    /**
     * 사용자의 잔액을 충전합니다. (낙관적락)
     *
     * @param command 잔액 충전 요청 객체
     * @return 충전된 잔액 정보를 포함한 응답 객체
     */
    public UserResult.ChargePointResult chargePoint(UserCommand.ChargePointCommand command) {
        // 1. 유저 정보 확인 (유저 정보 없을 시 예외 리턴)
        UserResult.UserInfoResult validateUser = userInfoService.validateUser(command.getUserId());
        try {
            // 2. 잔액 충전 및 리턴
            return userPointService.chargePoint(command);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(ErrorCode.DUPLICATE_REQUEST, e);
        }
    }
}
