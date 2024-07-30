package org.hhplus.ticketing.infra.queue.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.queue.QueueRepository;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.infra.queue.entity.QueueEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {

    private final QueueJpaRepository repository;

    /**
     * token을 기반으로 대기열 정보를 조회합니다.
     *
     * @param token 조회할 토큰
     * @return token에 해당하는 대기열 {@link Queue} 객체.
     */
    @Override
    public Optional<Queue> findByToken(UUID token) {
        return repository.findByToken(token).map(QueueEntity::toDomain);
    }

    /**
     * 대기열 정보를 저장합니다.
     *
     * @param domain 저장할 대기열 정보
     * @return 저장된 대기열 정보를 {@link Queue}으로 반환합니다.
     */
    @Override
    public Queue save(Queue domain) {
        return QueueEntity.toDomain(repository.save(QueueEntity.from(domain)));
    }
    /**
     * 주어진 상태를 가진 대기열의 수를 반환합니다.
     *
     * @param status 조회할 토큰의 상태
     * @return 주어진 상태를 가진 대기열의 수
     */
    @Override
    public Long countByStatus(Queue.Status status) {
        return repository.countByStatus(status);
    }

    /**
     * 가장 최근에 활성화된 대기열 정보를 반환합니다.
     *
     * @param status 조회할 대기열 객체의 상태
     * @return 주어진 상태를 가진 첫 번째 대기열 객체의 도메인 객체
     */
    @Override
    public Optional<Queue> getLastActiveQueue(Queue.Status status) {
        return repository.findFirstByStatusOrderByEnteredAtDesc(status).map(QueueEntity::toDomain);
    }


    /**
     * 만료 대상 토큰을 조회힙니다
     *
     * @param time 특정 시간 이전에 들어온 토큰을 필터링하기 위한 {@link LocalDateTime}
     * @return 주어진 상태와 특정 시간 이전에 들어온 {@link Queue} 객체 리스트
     */
    @Override
    public List<Queue> getExpiredTokens(LocalDateTime time) {
        return QueueEntity.toDomainList(repository.findActiveEnteredBeforeTime(time));
    }

    /**
     * 활성화 대상 토큰을 조회합니다
     *
     * @param pageable 결과의 크기와 페이징 정보를 제공하는 {@link Pageable}
     * @return 주어진 상태와 페이징 조건을 만족하는 큐 엔티티의 리스트
     */
    @Override
    public List<Queue> getActivatableTokens(Pageable pageable) {
        return QueueEntity.toDomainList(repository.findByWaitingOrderByCreatedAtAsc(pageable));
    }

    /**
     * 대기열 정보 리스트를 저장합니다.
     *
     * @param domains 저장할 {@link Queue} 객체 리스트
     * @return 저장된 {@link Queue} 객체 리스트
     */
    @Override
    public List<Queue> saveAll(List<Queue> domains) {
        List<QueueEntity> entities = domains.stream()
                .map(QueueEntity::from)
                .collect(Collectors.toList());

        List<QueueEntity> savedEntities = repository.saveAll(entities);

        return savedEntities.stream()
                .map(QueueEntity::toDomain)
                .collect(Collectors.toList());
    }
}
