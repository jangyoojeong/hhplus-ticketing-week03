package org.hhplus.ticketing.domain.user;

import org.hhplus.ticketing.domain.user.model.UserInfoDomain;

import java.util.Optional;

public interface UserInfoRepository {

    /**
     * userId를 기반으로 사용자 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return userId에 해당하는 사용자를 포함하는 Optional 객체
     */
    Optional<UserInfoDomain> findById(Long userId);
    
    /**
     * 사용자 정보를 저장합니다
     *
     * @param domain 저장할 사용자 정보
     * @return domain 저장된 사용자 정보
     */
    UserInfoDomain save(UserInfoDomain domain);
}
