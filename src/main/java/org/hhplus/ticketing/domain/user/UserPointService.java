package org.hhplus.ticketing.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.queue.QueueService;
import org.hhplus.ticketing.domain.user.model.*;
import org.hhplus.ticketing.domain.user.model.enums.PointType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointRepository userPointRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;

    /**
     * 사용자의 잔액을 충전합니다.
     *
     * @param command 잔액 충전 요청 객체
     * @return 충전된 잔액 정보를 포함한 응답 객체
     */
    @Transactional
    public UserResult.AddPointResult addUserPoint (UserCommand.AddPointCommand command) {

        // 1. 기존 잔액 조회 (기존 잔액 없을 시 default 0 리턴)
        UserPointDomain userPointDomain = userPointRepository.findByUserId(command.getUserId());

        // 2. 포인트 업데이트 (추가)
        userPointDomain.increasePoint(command.getAmount());
        UserResult.AddPointResult result = UserResult.AddPointResult.from(userPointRepository.save(userPointDomain));

        // 3. 히스토리 추가
        UserPointHistoryDomain historyDomain = UserPointHistoryDomain.builder()
                .userId(command.getUserId())
                .amount(command.getAmount())
                .type(PointType.CHARGE)
                .build();
        userPointHistoryRepository.save(historyDomain);

        // 4. 결과 반환
        return result;
    }

    /**
     * 사용자의 잔액을 차감합니다.
     *
     * @param command 잔액 차감 요청 객체
     * @return 차감된 잔액 정보를 포함한 응답 객체
     */
    @Transactional
    public UserResult.UsePointResult useUserPoint (UserCommand.UsePointCommand command) {

        // 1. 기존 잔액 조회
        UserPointDomain userPointDomain = userPointRepository.findByUserId(command.getUserId());

        // 2. 포인트 업데이트 (차감)
        // 포인트 부족할 시
        // INSUFFICIENT_POINTS 예외
        userPointDomain.decreasePoint(command.getAmount());
        UserResult.UsePointResult result = UserResult.UsePointResult.from(userPointRepository.save(userPointDomain));

        // 3. 히스토리 추가
        UserPointHistoryDomain historyDomain = UserPointHistoryDomain.builder()
                .userId(command.getUserId())
                .amount(command.getAmount())
                .type(PointType.USE)
                .build();
        userPointHistoryRepository.save(historyDomain);

        // 4. 결과 반환
        return result;
    }

    /**
     * 사용자의 잔액을 조회합니다.
     *
     * @param userId 잔액을 조회할 사용자의 ID
     * @return 잔액 result 객체
     */
    public UserResult.UserPointResult getUserPoint (Long userId) {
        UserPointDomain userPointDomain = userPointRepository.findByUserId(userId);
        log.info("사용자 ID {}의 잔여 포인트를 조회했습니다.", userId);
        return UserResult.UserPointResult.from(userPointDomain);
    }
}
