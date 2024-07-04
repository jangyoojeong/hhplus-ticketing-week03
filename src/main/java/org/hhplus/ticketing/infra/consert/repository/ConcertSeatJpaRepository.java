package org.hhplus.ticketing.infra.consert.repository;

import org.hhplus.ticketing.infra.consert.entity.ConcertSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeat, Long> {
}
