package org.hhplus.ticketing.domain.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.common.messaging.MessageSender;
import org.hhplus.ticketing.domain.outbox.model.Outbox;
import org.hhplus.ticketing.domain.outbox.model.OutboxCommand;
import org.hhplus.ticketing.domain.outbox.model.constants.OutboxConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final MessageSender messageSender;

    @Transactional
    public Outbox save(OutboxCommand.save command) {
        return outboxRepository.save(Outbox.from(command));
    }

    @Transactional
    public Outbox updateSent(OutboxCommand.updateSent command) {
        Outbox outbox = outboxRepository.getOutbox(command.getMessageKey(), command.getDomainType(), command.getEventType()).orElseThrow(()
                -> new CustomException(ErrorCode.OUTBOX_NOT_FOUND));
        return outboxRepository.save(outbox.setSent());
    }

    @Transactional
    public void retryFailedMessages() {
        LocalDateTime retryTargetTime = LocalDateTime.now().minusMinutes(OutboxConstants.OUTBOX_RETRY_THRESHOLD_MINUTES);
        List<Outbox> retryTargetList = outboxRepository.getRetryTargetList(retryTargetTime);

        retryTargetList.forEach(outbox -> {
            CompletableFuture<Boolean> future = messageSender.sendMessage(outbox.getEventType(), outbox.getMessageKey(), outbox.getMessage());
            boolean success = future.join();
            log.info("Message sending result for Outbox ID {}: {}", outbox.getOutboxId(), success);
            if (!success) return;
            outboxRepository.save(outbox.setSent());
        });
    }

}