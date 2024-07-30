package org.hhplus.ticketing.domain.queue;

import org.hhplus.ticketing.domain.queue.model.Queue;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QueueRepository {

    /**
     * 주어진 토큰을 기반으로 대기열 정보를 조회합니다.
     *
     * @param token 조회할 대기열 정보
     * @return domain 조회된 대기열 정보
     */
    Optional<Queue> findByToken(UUID token);

    /**
     * 대기열 정보를 저장합니다
     *
     * @param domain 저장할 대기열 정보
     * @return domain 저장된 대기열 정보
     */
    Queue save(Queue domain);

    /**
     * 주어진 상태를 가진 대기열의 수를 반환합니다.
     *
     * @param status 조회할 토큰의 상태
     * @return 주어진 상태를 가진 대기열의 수
     */
    Long countByStatus(Queue.Status status);

    /**
     * 가장 최근에 활성화된 대기열 정보를 반환합니다.
     *
     * @return 주어진 상태를 가진 첫 번째 대기열 객체의 도메인 객체
     */
    Optional<Queue> getLastActiveQueue(Queue.Status status);

    /**
     * 만료 대상 토큰을 조회힙니다
     *
     * @param time 특정 시간 이전에 들어온 토큰을 필터링하기 위한 {@link LocalDateTime}
     * @return 주어진 상태와 특정 시간 이전에 들어온 토큰의 목록
     */
    List<Queue> getExpiredTokens(LocalDateTime time);

    /**
     * 활성화 대상 토큰을 조회힙니다
     *
     * @return 주어진 상태와 특정 시간 이전에 들어온 토큰의 목록
     */
    List<Queue> getActivatableTokens(Pageable pageable);

    
    /**
     * 대기열 정보 리스트를 저장합니다.
     *
     * @param domains 저장할 대기열 정보 리스트
     * @return 저장된 대기열 정보 리스트
     */
    List<Queue> saveAll(List<Queue> domains);

}
