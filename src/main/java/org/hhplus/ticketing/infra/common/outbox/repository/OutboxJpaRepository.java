package org.hhplus.ticketing.infra.common.outbox.repository;

import org.hhplus.ticketing.infra.common.outbox.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity, Long> {
    Optional<OutboxEntity> findByMessageKeyAndDomainTypeAndEventType(String messageKey, String domainType, String eventType);
    List<OutboxEntity> findAllByIsSentFalseAndCreatedAtBefore(LocalDateTime retryTargetTime);
}
