package org.hhplus.ticketing.infra.user.repository;

import org.hhplus.ticketing.infra.user.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoJpaRepository extends JpaRepository<UserInfoEntity, Long> {
}
