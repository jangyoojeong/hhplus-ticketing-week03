package org.hhplus.ticketing.infra.common.outbox.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.outbox.model.Outbox;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "outbox")
public class OutboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id")
    private Long outboxId;

    @Column(name = "message_key", nullable = false)
    private String messageKey;

    @Column(name = "domain_type", nullable = false)
    private String domainType;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Lob
    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "is_sent", nullable = false)
    private boolean isSent = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;                        // 생성일자

    @Column(nullable = false)
    private LocalDateTime updatedAt;                        // 수정일자

    @PrePersist
    private void prePersist() {
        // `createdAt` 필드가 null일 때만 현재 시간으로 설정
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        // `updatedAt` 필드가 null일 때만 현재 시간으로 설정
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static OutboxEntity from(Outbox domain) {
        return OutboxEntity.builder()
                .outboxId(domain.getOutboxId())
                .messageKey(domain.getMessageKey())
                .domainType(domain.getDomainType())
                .eventType(domain.getEventType())
                .message(domain.getMessage())
                .isSent(domain.isSent())
                .sentAt(domain.getSentAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public Outbox toDomain() {
        return Outbox.builder()
                .outboxId(this.getOutboxId())
                .messageKey(this.getMessageKey())
                .domainType(this.getDomainType())
                .eventType(this.getEventType())
                .message(this.getMessage())
                .isSent(this.isSent())
                .sentAt(this.getSentAt())
                .createdAt(this.getCreatedAt())
                .build();
    }
}
