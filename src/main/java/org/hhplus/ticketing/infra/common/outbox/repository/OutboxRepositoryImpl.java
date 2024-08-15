package org.hhplus.ticketing.infra.common.outbox.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.outbox.model.Outbox;
import org.hhplus.ticketing.domain.outbox.OutboxRepository;
import org.hhplus.ticketing.infra.common.outbox.entity.OutboxEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public Outbox save(Outbox domain) {
        return outboxJpaRepository.save(OutboxEntity.from(domain)).toDomain();
    }

    @Override
    public Optional<Outbox> getOutbox(String messageKey, String domainType, String eventType) {
        return outboxJpaRepository.findByMessageKeyAndDomainTypeAndEventType(messageKey, domainType, eventType).map(OutboxEntity::toDomain);
    }

    @Override
    public List<Outbox> getRetryTargetList(LocalDateTime retryTargetTime) {
        return outboxJpaRepository.findAllByIsSentFalseAndCreatedAtBefore(retryTargetTime).stream()
                .map(OutboxEntity::toDomain)
                .collect(Collectors.toList());
    }
}
