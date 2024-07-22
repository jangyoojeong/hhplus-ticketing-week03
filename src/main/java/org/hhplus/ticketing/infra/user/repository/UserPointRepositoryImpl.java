package org.hhplus.ticketing.infra.user.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.user.UserPointRepository;
import org.hhplus.ticketing.domain.user.model.UserPoint;
import org.hhplus.ticketing.infra.user.entity.UserPointEntity;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointJpaRepository repository;

    /**
     * 사용자 포인트 정보를 조회합니다
     *
     * @param userId 포인트 조회할 사용자 ID
     * @return domain 조회된 포인트 정보
     */
    @Override
    public Optional<UserPoint> findByUserId(Long userId) {
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
