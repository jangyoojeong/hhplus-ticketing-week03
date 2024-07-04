package org.hhplus.ticketing.infra.consert.repository;

import org.hhplus.ticketing.infra.consert.entity.Concert;
import org.hhplus.ticketing.infra.consert.entity.ConcertOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertOptionJpaRepository extends JpaRepository<ConcertOption, Long> {
}
