package org.hhplus.ticketing.domain.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Queue {

    private Long queueId;                        // 대기열ID (키값)
    private Long userId;                         // 유저ID
    private UUID token;                          // 발급된 토큰
    private Status status;                       // 토큰상태 (ACTIVE/WAITING/EXPIRED)
    private LocalDateTime enteredAt;             // 입장시간
    private LocalDateTime createAt;              // 생성시간

    public static Queue createActive(Long userId) {
        return Queue.builder()
                .userId(userId)
                .token(UUID.randomUUID())
                .status(Status.ACTIVE)
                .enteredAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .build();
    }

    public static Queue createWaiting(Long userId) {
        return Queue.builder()
                .userId(userId)
                .token(UUID.randomUUID())
                .status(Status.WAITING)
                .createAt(LocalDateTime.now())
                .build();
    }

    public Long getQueuePosition(Optional<Queue> lastActiveQueue) {
        return lastActiveQueue
                .map(queue -> this.queueId - queue.getQueueId())
                .orElse(0L);
    }

    public Queue setExpired() {
        this.status = Status.EXPIRED;
        return this;
    }

    public Queue setActive() {
        this.status = Status.ACTIVE;
        this.enteredAt = LocalDateTime.now();
        return this;
    }

    public void validateActiveStatus() {
        if (this.status != Status.ACTIVE) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public enum Status {
        ACTIVE,     // 활성화
        WAITING,    // 대기중
        EXPIRED     // 만료
    }
}
