package org.hhplus.ticketing.infra.queue.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.queue.model.QueueDomain;
import org.hhplus.ticketing.domain.queue.model.enums.TokenStatus;
import org.hhplus.ticketing.domain.user.model.UserInfoDomain;
import org.hhplus.ticketing.infra.user.entity.UserInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "queue")
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id", nullable = false, updatable = false)
    private Long queueId;                   // 대기열ID (키값)

    @Column(name = "user_id", nullable = false)
    private Long userId;                    // 유저ID

    @Column(name = "token", nullable = false, unique = true, updatable = false, columnDefinition = "BINARY(16)")
    private UUID token;                     // 발급된 토큰

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TokenStatus status;             // 토큰상태 (ACTIVE/WAITING/EXPIRED)

    @Column(name = "entered_at")
    private LocalDateTime enteredAt;        // 입장시간

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;         // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;         // 수정일자

    @PrePersist
    private void prePersist() {
        if (token == null) {
            token = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Queue from(QueueDomain domain) {
        return Queue.builder()
                .queueId(domain.getQueueId())
                .userId(domain.getUserId())
                .token(domain.getToken())
                .status(domain.getStatus())
                .enteredAt(domain.getEnteredAt())
                .build();
    }

    public static QueueDomain toDomain(Queue entity) {
        return QueueDomain.builder()
                .queueId(entity.getQueueId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .status(entity.getStatus())
                .enteredAt(entity.getEnteredAt())
                .build();
    }

    public static List<QueueDomain> toDomainList(List<Queue> entityList) {
        return entityList.stream()
                .map(Queue::toDomain)
                .collect(Collectors.toList());
    }
}
