package org.hhplus.ticketing.infra.concert.repository;

import org.hhplus.ticketing.infra.concert.entity.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ConcertJpaRepository  extends JpaRepository<ConcertEntity, Long> {


}
