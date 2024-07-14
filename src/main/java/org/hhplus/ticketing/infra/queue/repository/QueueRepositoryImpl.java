package org.hhplus.ticketing.infra.queue.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.queue.QueueRepository;
import org.hhplus.ticketing.domain.queue.model.QueueDomain;
import org.hhplus.ticketing.domain.queue.model.enums.TokenStatus;
import org.hhplus.ticketing.infra.queue.entity.Queue;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {

    private final QueueJpaRepository repository;

    /**
     * token을 기반으로 대기열 정보를 조회합니다.
     *
     * @param token 조회할 토큰
     * @return token에 해당하는 대기열 {@link QueueDomain} 객체.
     */
    @Override
    public Optional<QueueDomain> findByToken(UUID token) {
        return repository.findByToken(token).map(Queue::toDomain);
    }

    /**
     * 대기열 정보를 저장합니다.
     *
     * @param domain 저장할 대기열 정보
     * @return 저장된 대기열 정보를 {@link QueueDomain}으로 반환합니다.
     */
    @Override
    public QueueDomain save(QueueDomain domain) {
        return Queue.toDomain(repository.save(Queue.from(domain)));
    }
    /**
     * 주어진 상태를 가진 대기열의 수를 반환합니다.
     *
     * @param status 조회할 토큰의 상태
     * @return 주어진 상태를 가진 대기열의 수
     */
    @Override
    public Long countByStatus(TokenStatus status) {
        return repository.countByStatus(status);
    }

    /**
     * 가장 최근에 활성화된 대기열 정보를 반환합니다.
     *
     * @param status 조회할 대기열 객체의 상태
     * @return 주어진 상태를 가진 첫 번째 대기열 객체의 도메인 객체
     */
    @Override
    public Optional<QueueDomain> getLastActiveQueue(TokenStatus status) {
        return repository.findFirstByStatusOrderByEnteredAtDesc(status).map(Queue::toDomain);
    }

    /**
     * 만료 대상 토큰을 조회힙니다
     *
     * @param status 조회할 토큰의 상태를 나타내는 {@link TokenStatus}
     * @param time 특정 시간 이전에 들어온 토큰을 필터링하기 위한 {@link LocalDateTime}
     * @return 주어진 상태와 특정 시간 이전에 들어온 {@link QueueDomain} 객체 리스트
     */
    @Override
    public List<QueueDomain> findActiveTokensEnteredBefore(TokenStatus status, LocalDateTime time) {
        return Queue.toDomainList(repository.findActiveTokensEnteredBefore(status, time));
    }

    /**
     * 활성화 대상 토큰을 조회합니다
     *
     * @param status 조회할 큐의 상태를 나타내는 {@link String}
     * @param pageable 결과의 크기와 페이징 정보를 제공하는 {@link Pageable}
     * @return 주어진 상태와 페이징 조건을 만족하는 큐 엔티티의 리스트
     */
    @Override
    public List<QueueDomain> findByStatusOrderByCreatedAtAsc(TokenStatus status, Pageable pageable) {
        return Queue.toDomainList(repository.findByStatusOrderByCreatedAtAsc(status, pageable));
    }

    /**
     * 대기열 정보 리스트를 저장합니다.
     *
     * @param domains 저장할 {@link QueueDomain} 객체 리스트
     * @return 저장된 {@link QueueDomain} 객체 리스트
     */
    @Override
    public List<QueueDomain> saveAll(List<QueueDomain> domains) {
        List<Queue> entities = domains.stream()
                .map(Queue::from)
                .collect(Collectors.toList());

        List<Queue> savedEntities = repository.saveAll(entities);

        return savedEntities.stream()
                .map(Queue::toDomain)
                .collect(Collectors.toList());
    }
}
