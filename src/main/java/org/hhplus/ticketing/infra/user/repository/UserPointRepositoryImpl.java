package org.hhplus.ticketing.infra.user.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.user.UserPointRepository;
import org.hhplus.ticketing.domain.user.model.UserPoint;
import org.hhplus.ticketing.infra.user.entity.UserPointEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointJpaRepository repository;

    @Override
    public Optional<UserPoint> getUserPoint(Long userId) {
        return repository.findByUserId(userId).map(UserPointEntity::toDomain);
    }

    /**
     * 사용자 포인트 정보를 저장합니다
     *
     * @param domain 포인트 저장할 사용자 ID
     * @return domain 저장된 포인트 정보
     */
    @Override
    public UserPoint save(UserPoint domain) {
        return repository.save(UserPointEntity.from(domain)).toDomain();
    }

}
