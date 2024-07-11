package org.hhplus.ticketing.infra.consert.repository;

import org.hhplus.ticketing.infra.consert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertJpaRepository  extends JpaRepository<Concert, Long> {



}
