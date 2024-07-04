package org.hhplus.ticketing.infra.queue.repository;

import org.hhplus.ticketing.infra.queue.entity.EnterHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterHistoryJpaRepository extends JpaRepository<EnterHistory, Long> {
}
