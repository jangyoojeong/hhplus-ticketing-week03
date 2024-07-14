package org.hhplus.ticketing.infra.concert.repository;

import org.hhplus.ticketing.domain.concert.model.enums.ReservationStatus;
import org.hhplus.ticketing.infra.concert.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationIdAndStatus(Long reservationId, ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'RESERVED' AND r.reservationAt < :time")
    List<Reservation> findReservedBefore(@Param("time") LocalDateTime time);

    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.status = :status, r.updatedAt = CURRENT_TIMESTAMP WHERE r.reservationId = :reservationId")
    int updateReservationStatus(@Param("reservationId") Long reservationId, @Param("status") ReservationStatus status);
}
