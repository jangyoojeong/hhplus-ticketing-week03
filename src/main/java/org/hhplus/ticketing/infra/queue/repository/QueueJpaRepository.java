package org.hhplus.ticketing.infra.queue.repository;

import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.infra.queue.entity.QueueEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QueueJpaRepository  extends JpaRepository<QueueEntity, Long> {

    /**
     * 주어진 토큰을 기반으로 대기열 정보를 조회합니다.
     *
     * @param token 조회할 대기열 정보
     * @return token에 해당하는 대기열 {@link Optional} 객체
     */
    Optional<QueueEntity> findByToken(UUID token);

    /**
     * 주어진 상태를 가진 Queue 엔티티의 수를 반환합니다.
     *
     * @param status 조회할 Queue의 상태
     * @return 주어진 상태를 가진 Queue 엔티티의 수
     */
    Long countByStatus(Queue.Status status);

    /**
     * 가장 최근에 활성화된 대기열 정보를 반환합니다.
     *
     * @param status 조회할 토큰의 상태
     * @return 주어진 상태에 해당하는 가장 최근에 생성된 대기열 항목을 포함하는 Optional 객체
     */
    Optional<QueueEntity> findFirstByStatusOrderByEnteredAtDesc(Queue.Status status);

    /**
     * 만료 대상 토큰을 조회힙니다
     *
     * @param status 토큰의 상태를 나타내는 {@link Queue.Status}
     * @param time 특정 시간 이전에 들어온 토큰을 필터링하기 위한 {@link LocalDateTime}
     * @return 주어진 상태와 특정 시간 이전에 들어온 토큰의 목록
     */
    @Query("SELECT t FROM QueueEntity t WHERE t.status = :status AND t.enteredAt < :time")
    List<QueueEntity> findActiveTokensEnteredBefore(@Param("status") Queue.Status status, @Param("time") LocalDateTime time);

    /**
     * 활성화 대상 토큰을 조회합니다
     *
     * @param status 조회할 큐의 상태를 나타내는 {@link String}
     * @param pageable 결과의 크기와 페이징 정보를 제공하는 {@link Pageable}
     * @return 주어진 상태와 페이징 조건을 만족하는 큐 엔티티의 리스트
     */
    @Query("SELECT t FROM QueueEntity t WHERE t.status = :status ORDER BY t.createdAt ASC")
    List<QueueEntity> findByStatusOrderByCreatedAtAsc(@Param("status") Queue.Status status, Pageable pageable);

}
