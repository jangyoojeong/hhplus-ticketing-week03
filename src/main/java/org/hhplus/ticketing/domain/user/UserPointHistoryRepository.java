package org.hhplus.ticketing.domain.user;

import org.hhplus.ticketing.domain.user.model.UserPointHistory;

import java.util.List;

public interface UserPointHistoryRepository {

    /**
     * userId를 기반으로 포인트 히스토리 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return userId에 해당하는 유저포인트 히스토리 도메인 객체
     */
    List<UserPointHistory> findByUserId(Long userId);

    /**
     * 포인트 히스토리 정보를 저장합니다.
     *
     * @param domain 저장할 유저포인트 히스토리 도메인 객체
     * @return domain 저장된 유저포인트 히스토리 도메인 객체
     */
    UserPointHistory save(UserPointHistory domain);

}
