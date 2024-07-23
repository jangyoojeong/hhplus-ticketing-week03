package org.hhplus.ticketing.infra.concert.repository;

import org.hhplus.ticketing.infra.concert.entity.ConcertSeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeatEntity, Long> {

    @Query("SELECT cs FROM ConcertSeatEntity cs WHERE cs.concertSeatId = :concertSeatId AND cs.status = 'AVAILABLE'")
    Optional<ConcertSeatEntity> findAvailableSeatById(@Param("concertSeatId") Long concertSeatId);

    List<ConcertSeatEntity> findByConcertSeatIdIn(List<Long> concertSeatIds);

    @Query("SELECT cs FROM ConcertSeatEntity cs WHERE cs.concertOptionId = :concertOptionId AND cs.status = 'AVAILABLE'")
    List<ConcertSeatEntity> findByConcertOptionId(@Param("concertOptionId") Long concertOptionId);

}
