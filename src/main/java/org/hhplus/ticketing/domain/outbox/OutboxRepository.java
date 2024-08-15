package org.hhplus.ticketing.domain.outbox;

import org.hhplus.ticketing.domain.outbox.model.Outbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxRepository {
    Outbox save(Outbox domain);
    Optional<Outbox> getOutbox(String messageKey, String domainType, String eventType);
    List<Outbox> getRetryTargetList(LocalDateTime retryTargetTime);
}
