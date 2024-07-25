package org.hhplus.ticketing.infra.concert.repository;

import org.hhplus.ticketing.infra.concert.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

    @Query("SELECT r FROM ReservationEntity r WHERE r.reservationId = :reservationId AND r.status = 'RESERVED'")
    Optional<ReservationEntity> findByReservationId(@Param("reservationId") Long reservationId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.status = 'RESERVED' AND r.reservationAt < :time")
    List<ReservationEntity> findReservedBefore(@Param("time") LocalDateTime time);

    List<ReservationEntity> findByUserId(Long userId);

    List<ReservationEntity> findByConcertSeatId(Long concertSeatId);
}
