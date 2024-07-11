package org.hhplus.ticketing.infra.consert.repository;

import org.hhplus.ticketing.infra.consert.entity.ConcertSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeat, Long> {

    @Query("SELECT cs FROM ConcertSeat cs WHERE cs.concertSeatId = :concertSeatId AND cs.status = 'AVAILABLE'")
    Optional<ConcertSeat> findAvailableSeatById(@Param("concertSeatId") Long concertSeatId);

    List<ConcertSeat> findByConcertSeatIdIn(List<Long> concertSeatIds);

    @Query("SELECT cs FROM ConcertSeat cs WHERE cs.concertOptionId = :concertOptionId AND cs.status = 'AVAILABLE'")
    List<ConcertSeat> findByConcertOptionIdAndStatus(@Param("concertOptionId") Long concertOptionId);

}
