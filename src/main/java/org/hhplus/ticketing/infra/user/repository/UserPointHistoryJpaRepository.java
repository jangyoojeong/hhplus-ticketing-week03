package org.hhplus.ticketing.infra.user.repository;

import org.hhplus.ticketing.infra.user.entity.UserPointHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPointHistoryJpaRepository extends JpaRepository<UserPointHistoryEntity, Long> {

    /**
     * userId를 기반으로 사용자 포인트 히스토리 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return userId에 해당하는 포인트 히스토리 {@link List} 객체. 만일 해당하는 사용자가 없으면 빈 리스트를 반환합니다.
     */
    List<UserPointHistoryEntity> findByUserId(Long userId);
}
