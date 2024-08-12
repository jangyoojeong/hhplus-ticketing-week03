package org.hhplus.ticketing.application.user;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.user.UserInfoService;
import org.hhplus.ticketing.domain.user.UserPointService;
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
    public UserResult.GetPoint getPointResult(Long userId) {
        return UserResult.GetPoint.from(userPointService.getPointResult(userId));
    }

    /**
     * 사용자의 잔액을 충전합니다. (낙관적락)
     *
     * @param criteria 잔액 충전 요청 객체
     * @return 충전된 잔액 정보를 포함한 응답 객체
     */
    public UserResult.ChargePoint chargePoint(UserCriteria.ChargePoint criteria) {
        // 1. 유저 정보 확인 (유저 정보 없을 시 예외 리턴)
        userInfoService.validateUser(criteria.getUserId());

        // 2. 잔액 충전 및 리턴
        return UserResult.ChargePoint.from(userPointService.chargePoint(criteria.toCommand()));
    }
}
