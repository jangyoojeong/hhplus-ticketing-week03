package org.hhplus.ticketing.domain.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.queue.model.constants.QueueConstants;
import org.hhplus.ticketing.domain.queue.model.enums.TokenStatus;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserPointDomain;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueDomain {

    private Long queueId;                        // 대기열ID (키값)
    private Long userId;                         // 유저ID
    private UUID token;                          // 발급된 토큰
    private TokenStatus status;                  // 토큰상태 (ACTIVE/WAITING/EXPIRED)
    private LocalDateTime enteredAt;             // 입장시간
    private LocalDateTime createAt;              // 생성시간

    public static QueueDomain createActiveQueue(Long userId) {
        return QueueDomain.builder()
                .userId(userId)
                .status(TokenStatus.ACTIVE)
                .enteredAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .build();
    }

    public static QueueDomain createWaitingQueue(Long userId) {
        return QueueDomain.builder()
                .userId(userId)
                .status(TokenStatus.WAITING)
                .createAt(LocalDateTime.now())
                .build();
    }

    public QueueDomain updateQueueExpired() {
        return QueueDomain.builder()
                .queueId(this.queueId)
                .userId(this.userId)
                .token(this.token)
                .status(TokenStatus.EXPIRED)
                .enteredAt(this.enteredAt)
                .build();
    }

    public QueueDomain updateQueueActive() {
        return QueueDomain.builder()
                .queueId(this.queueId)
                .userId(this.userId)
                .token(this.token)
                .status(TokenStatus.ACTIVE)
                .enteredAt(LocalDateTime.now())
                .build();
    }

}
