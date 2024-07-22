package org.hhplus.ticketing.infra.user.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.user.UserInfoRepository;
import org.hhplus.ticketing.domain.user.model.UserInfo;
import org.hhplus.ticketing.infra.user.entity.UserInfoEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserInfoRepositoryImpl implements UserInfoRepository {

    private final UserInfoJpaRepository repository;

    /**
     * userId를 기반으로 사용자 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return userId에 해당하는 사용자를 포함하는 {@link UserInfo} 객체. 사용자가 존재하지 않을 경우 {@link IllegalArgumentException}을 발생시킵니다.
     */
    @Override
    public Optional<UserInfo> findById(Long userId) {
        return repository.findById(userId).map(UserInfoEntity::toDomain);
    }

    /**
     * 사용자 정보를 저장합니다.
     *
     * @param domain 저장할 사용자 정보
     * @return 저장된 사용자 정보를 {@link UserInfo}으로 반환합니다.
     */
    @Override
    public UserInfo save(UserInfo domain) {
        return repository.save(UserInfoEntity.from(domain)).toDomain();
    }
}
