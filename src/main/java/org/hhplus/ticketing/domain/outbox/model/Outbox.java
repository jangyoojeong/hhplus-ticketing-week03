package org.hhplus.ticketing.domain.outbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outbox {

    private Long outboxId;             // outbox ID
    private String messageKey;         // 도메인 식별 키
    private String domainType;         // 도메인 타입 (예: Payment, Concert 등)
    private String eventType;          // 이벤트 타입 (예: PaymentSuccess 등)
    private String message;            // 메시지의 실제 내용 (JSON 형태)
    private boolean isSent;            // 메시지가 전송되었는지 여부
    private LocalDateTime sentAt;      // 메시지 전송시점
    private LocalDateTime createdAt;   // 생성시간

    public static Outbox from(OutboxCommand.Save command) {
        return Outbox.builder()
                .messageKey(command.getMessageKey())
                .domainType(command.getDomainType())
                .eventType(command.getEventType())
                .message(command.getMessage())
                .build();
    }

    public Outbox setSent() {
        if (this.isSent) throw new CustomException(ErrorCode.INVALID_STATE);

        this.isSent = true;
        this.sentAt = LocalDateTime.now();
        return this;
    }
}
