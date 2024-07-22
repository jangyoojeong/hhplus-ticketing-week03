package org.hhplus.ticketing.infra.user.repository;

import org.hhplus.ticketing.infra.user.entity.UserPointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPointJpaRepository extends JpaRepository<UserPointEntity, Long> {

    /**
     * userId를 기반으로 사용자 포인트 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return userId에 해당하는 포인트 {@link Optional} 객체. 만일 해당하는 사용자가 없으면 비어있는 Optional을 반환합니다.
     */
    Optional<UserPointEntity> findByUserId(Long userId);
}
