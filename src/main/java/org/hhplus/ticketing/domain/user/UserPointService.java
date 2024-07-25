package org.hhplus.ticketing.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserPoint;
import org.hhplus.ticketing.domain.user.model.UserPointHistory;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointRepository userPointRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;

    /**
     * 주어진 사용자 ID로 포인트 정보를 조회합니다.
     * 포인트 정보가 없을 경우 기본값 0을 가진 새로운 포인트 객체를 생성합니다.
     *
     * @param userId 포인트를 조회할 사용자 ID
     * @return 사용자의 포인트 정보
     */
    private UserPoint getOrCreatePoint (Long userId) {
        return userPointRepository.findByUserId(userId)
                .orElse(UserPoint.create(userId));
    }

    /**
     * 사용자의 잔액을 조회합니다.
     *
     * @param userId 잔액을 조회할 사용자의 ID
     * @return 잔액 result 객체
     */
    @Transactional(readOnly = true)
    public UserResult.UserPointResult getPoint (Long userId) {
        return UserResult.UserPointResult.from(getOrCreatePoint(userId));
    }

    /**
     * 사용자의 잔액을 충전합니다.
     *
     * @param command 잔액 충전 요청 객체
     * @return 충전된 잔액 정보를 포함한 응답 객체
     */
    @Transactional(rollbackFor = {Exception.class})
    public UserResult.ChargePointResult chargePoint (UserCommand.ChargePointCommand command) {
        UserPoint userPoint = getOrCreatePoint(command.getUserId());
        userPoint.chargePoint(command.getAmount());
        UserResult.ChargePointResult result = UserResult.ChargePointResult.from(userPointRepository.save(userPoint));
        saveHistory(command.getUserId(), command.getAmount(), UserPointHistory.Type.CHARGE);
        return result;
    }

    /**
     * 사용자의 잔액을 사용합니다.
     *
     * @param command 잔액 차감 요청 객체
     * @return 차감된 잔액 정보를 포함한 응답 객체
     */
    @Transactional(rollbackFor = {Exception.class})
    public UserResult.UsePointResult usePoint (UserCommand.UsePointCommand command) {
        UserPoint userPoint = getOrCreatePoint(command.getUserId());
        userPoint.usePoint(command.getAmount());
        UserResult.UsePointResult result = UserResult.UsePointResult.from(userPointRepository.save(userPoint));
        saveHistory(command.getUserId(), command.getAmount(), UserPointHistory.Type.USE);
        return result;
    }

    /**
     * 포인트 히스토리를 저장합니다.
     *
     * @param userId 포인트를 기록할 사용자 ID
     * @param amount 포인트 양
     * @param type 포인트 변동 타입 (충전/사용)
     */
    private void saveHistory (Long userId, int amount, UserPointHistory.Type type) {
        userPointHistoryRepository.save(UserPointHistory.create(userId, amount, type));
    }
}
