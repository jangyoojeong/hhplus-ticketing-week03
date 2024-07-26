package org.hhplus.ticketing.infra.concert.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.concert.ConcertRepository;
import org.hhplus.ticketing.domain.concert.model.Concert;
import org.hhplus.ticketing.domain.concert.model.ConcertOption;
import org.hhplus.ticketing.domain.concert.model.ConcertSeat;
import org.hhplus.ticketing.domain.concert.model.Reservation;
import org.hhplus.ticketing.infra.concert.entity.ConcertEntity;
import org.hhplus.ticketing.infra.concert.entity.ConcertOptionEntity;
import org.hhplus.ticketing.infra.concert.entity.ConcertSeatEntity;
import org.hhplus.ticketing.infra.concert.entity.ReservationEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertOptionJpaRepository concertOptionJpaRepository;
    private final ConcertSeatJpaRepository concertSeatJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Optional<ConcertSeat> getAvailableSeat(Long concertSeatId) {
        return concertSeatJpaRepository.findAvailableSeatById(concertSeatId).map(ConcertSeatEntity::toDomain);
    }

    @Override
    public List<ConcertSeat> getAvailableSeats(Long concertOptionId) {
        return concertSeatJpaRepository.findByConcertOptionId(concertOptionId).stream()
                .map(ConcertSeatEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ConcertSeat saveSeat(ConcertSeat domain) {
        return concertSeatJpaRepository.save(ConcertSeatEntity.from(domain)).toDomain();
    }

    @Override
    public Optional<Reservation> getActiveReservation(Long reservationId) {
        return reservationJpaRepository.findByReservationId(reservationId).map(ReservationEntity::toDomain);
    }

    @Override
    public Reservation saveReservation(Reservation domain) {
        return reservationJpaRepository.save(ReservationEntity.from(domain)).toDomain();
    }

    @Override
    public List<Reservation> getExpiredReservations(LocalDateTime time) {
        return reservationJpaRepository.findReservedBefore(time).stream()
                .map(ReservationEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> saveAllReservation(List<Reservation> domains) {
        List<ReservationEntity> entities = domains.stream()
                .map(ReservationEntity::from)
                .collect(Collectors.toList());

        List<ReservationEntity> savedEntities = reservationJpaRepository.saveAll(entities);

        return savedEntities.stream()
                .map(ReservationEntity::toDomain)
                .collect(Collectors.toList());
    }



    @Override
    public List<ConcertSeat> saveAllSeat(List<ConcertSeat> domains) {
        List<ConcertSeatEntity> entities = domains.stream()
                .map(ConcertSeatEntity::from)
                .collect(Collectors.toList());

        List<ConcertSeatEntity> savedEntities = concertSeatJpaRepository.saveAll(entities);

        return savedEntities.stream()
                .map(ConcertSeatEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConcertSeat> getSeats(List<Long> concertSeatIds) {
        return concertSeatJpaRepository.findByConcertSeatIdIn(concertSeatIds).stream()
                .map(ConcertSeatEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ConcertSeat> findSeatById(Long concertSeatId) {
        return concertSeatJpaRepository.findById(concertSeatId).map(ConcertSeatEntity::toDomain);
    }

    @Override
    public List<ConcertOption> getAvailableDates(Long concertId, LocalDateTime currentDateTime) {
        return concertOptionJpaRepository.findByConcertIdAndConcertAtAfter(concertId, currentDateTime).stream()
                .map(ConcertOptionEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Concert saveConcert(Concert domain) {
        return concertJpaRepository.save(ConcertEntity.from(domain)).toDomain();
    }

    @Override
    public ConcertOption saveConcertOption(ConcertOption domain) {
        return concertOptionJpaRepository.save(ConcertOptionEntity.from(domain)).toDomain();
    }

    @Override
    public List<Reservation> findByUserId(Long userId) {
        return reservationJpaRepository.findByUserId(userId).stream()
                .map(ReservationEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByConcertSeatId(Long concertSeatId) {
        return reservationJpaRepository.findByConcertSeatId(concertSeatId).stream()
                .map(ReservationEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Reservation> findReservationById(Long reservationId) {
        return reservationJpaRepository.findById(reservationId).map(ReservationEntity::toDomain);
    }
}
