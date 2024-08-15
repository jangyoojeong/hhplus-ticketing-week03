package org.hhplus.ticketing.domain.outbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OutboxCommand {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class save {
        private String messageKey; // 도메인 식별 키
        private String domainType; // 도메인 타입 (예: Payment, Concert 등)
        private String eventType;  // 이벤트 타입 (예: PaymentSuccess 등)
        private String message;    // 메시지의 실제 내용 (JSON 형태)
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class updateSent {
        private String messageKey; // 도메인 식별 키
        private String domainType; // 도메인 타입 (예: Payment, Concert 등)
        private String eventType;  // 이벤트 타입 (예: PaymentSuccess 등)
    }
}
