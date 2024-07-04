package org.hhplus.ticketing.infra.user.repository;

import org.hhplus.ticketing.infra.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
}
