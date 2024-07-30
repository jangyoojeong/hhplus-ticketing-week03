package org.hhplus.ticketing.infra.user.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.user.UserPointHistoryRepository;
import org.hhplus.ticketing.domain.user.model.UserPointHistory;
import org.hhplus.ticketing.infra.user.entity.UserPointHistoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserPointHistoryRepositoryImpl implements UserPointHistoryRepository {

    private final UserPointHistoryJpaRepository repository;

    /**
     * 포인트 히스토리 정보를 저장합니다.
     *
     * @param domain 저장할 유저포인트 히스토리 도메인 객체
     * @return domain 저장된 유저포인트 히스토리 도메인 객체
     */
    @Override
    public UserPointHistory save(UserPointHistory domain) {
        return UserPointHistoryEntity.toDomain(repository.save(UserPointHistoryEntity.from(domain)));
    }
}
