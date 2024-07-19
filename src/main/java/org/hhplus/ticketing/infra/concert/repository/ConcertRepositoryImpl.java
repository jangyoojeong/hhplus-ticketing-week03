package org.hhplus.ticketing.infra.concert.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.concert.ConcertRepository;
import org.hhplus.ticketing.domain.concert.model.ConcertDomain;
import org.hhplus.ticketing.domain.concert.model.ConcertOptionDomain;
import org.hhplus.ticketing.domain.concert.model.ConcertSeatDomain;
import org.hhplus.ticketing.domain.concert.model.ReservationDomain;
import org.hhplus.ticketing.domain.concert.model.enums.ReservationStatus;
import org.hhplus.ticketing.infra.concert.entity.ConcertSeat;
import org.hhplus.ticketing.infra.concert.entity.Concert;
import org.hhplus.ticketing.infra.concert.entity.ConcertOption;
import org.hhplus.ticketing.infra.concert.entity.Reservation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertOptionJpaRepository concertOptionJpaRepository;
    private final ConcertSeatJpaRepository concertSeatJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Optional<ConcertSeatDomain> findAvailableSeatById(Long concertSeatId) {
        return concertSeatJpaRepository.findAvailableSeatById(concertSeatId).map(ConcertSeat::toDomain);
    }

    @Override
    public List<ConcertSeatDomain> findByConcertOptionIdAndStatus(Long concertOptionId) {
        return concertSeatJpaRepository.findByConcertOptionIdAndStatus(concertOptionId).stream()
                .map(ConcertSeat::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ConcertSeatDomain saveSeat(ConcertSeatDomain domain) {
        return concertSeatJpaRepository.save(ConcertSeat.from(domain)).toDomain();
    }

    @Override
    public Optional<ReservationDomain> findByReservationIdAndStatus(Long reservationId, ReservationStatus status) {
        return reservationJpaRepository.findByReservationIdAndStatus(reservationId, status).map(Reservation::toDomain);
    }

    @Override
    public ReservationDomain saveReservation(ReservationDomain domain) {
        return reservationJpaRepository.save(Reservation.from(domain)).toDomain();
    }

    @Override
    public List<ReservationDomain> findReservedBefore(LocalDateTime time) {
        return reservationJpaRepository.findReservedBefore(time).stream()
                .map(Reservation::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int updateReservationStatus(Long reservationId, ReservationStatus status) {
        return reservationJpaRepository.updateReservationStatus(reservationId, status);
    }

    @Override
    public List<ReservationDomain> saveAllReservation(List<ReservationDomain> domains) {
        List<Reservation> entities = domains.stream()
                .map(Reservation::from)
                .collect(Collectors.toList());

        List<Reservation> savedEntities = reservationJpaRepository.saveAll(entities);

        return savedEntities.stream()
                .map(Reservation::toDomain)
                .collect(Collectors.toList());
    }



    @Override
    public List<ConcertSeatDomain> saveAllSeat(List<ConcertSeatDomain> domains) {
        List<ConcertSeat> entities = domains.stream()
                .map(ConcertSeat::from)
                .collect(Collectors.toList());

        List<ConcertSeat> savedEntities = concertSeatJpaRepository.saveAll(entities);

        return savedEntities.stream()
                .map(ConcertSeat::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConcertSeatDomain> findByConcertSeatIdIn(List<Long> concertSeatIds) {
        return concertSeatJpaRepository.findByConcertSeatIdIn(concertSeatIds).stream()
                .map(ConcertSeat::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ConcertSeatDomain> findSeatById(Long concertSeatId) {
        return concertSeatJpaRepository.findById(concertSeatId).map(ConcertSeat::toDomain);
    }

    @Override
    public List<ConcertOptionDomain> findByConcertIdAndConcertAtAfter(Long concertId, LocalDateTime currentDateTime) {
        return concertOptionJpaRepository.findByConcertIdAndConcertAtAfter(concertId, currentDateTime).stream()
                .map(ConcertOption::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ConcertDomain saveConcert(ConcertDomain domain) {
        return concertJpaRepository.save(Concert.from(domain)).toDomain();
    }

    @Override
    public ConcertOptionDomain saveConcertOption(ConcertOptionDomain domain) {
        return concertOptionJpaRepository.save(ConcertOption.from(domain)).toDomain();
    }

    @Override
    public List<ReservationDomain> findByUserId(Long userId) {
        return reservationJpaRepository.findByUserId(userId).stream()
                .map(Reservation::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReservationDomain> findReservationById(Long reservationId) {
        return reservationJpaRepository.findById(reservationId).map(Reservation::toDomain);
    }
}
