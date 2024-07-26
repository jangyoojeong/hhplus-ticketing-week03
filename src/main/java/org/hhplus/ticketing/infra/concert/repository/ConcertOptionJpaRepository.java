package org.hhplus.ticketing.infra.concert.repository;

import org.hhplus.ticketing.infra.concert.entity.ConcertOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertOptionJpaRepository extends JpaRepository<ConcertOptionEntity, Long> {

    /**
     * 주어진 콘서트 ID와 현재 시간 이후의 콘서트 일정을 가진 콘서트 옵션들을 조회합니다.
     *
     * @param concertId 조회할 콘서트의 ID
     * @param currentDateTime 현재 시간
     * @return 주어진 콘서트 ID와 현재 시간 이후의 콘서트 일정을 가진 콘서트 옵션 리스트
     */
    List<ConcertOptionEntity> findByConcertIdAndConcertAtAfter(Long concertId, LocalDateTime currentDateTime);
}