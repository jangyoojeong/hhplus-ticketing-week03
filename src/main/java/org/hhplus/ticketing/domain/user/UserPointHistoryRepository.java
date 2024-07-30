package org.hhplus.ticketing.domain.user;

import org.hhplus.ticketing.domain.user.model.UserPointHistory;

public interface UserPointHistoryRepository {

    /**
     * 포인트 히스토리 정보를 저장합니다.
     *
     * @param domain 저장할 유저포인트 히스토리 도메인 객체
     * @return domain 저장된 유저포인트 히스토리 도메인 객체
     */
    UserPointHistory save(UserPointHistory domain);

}
