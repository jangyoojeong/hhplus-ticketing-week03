package org.hhplus.ticketing.domain.concert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 도메인 객체: ConcertOption
 * 콘서트 개최 정보 관리.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertOption {
    private Long concertOptionId;       // 콘서트옵션ID (키값)
    private Long concertId;             // 콘서트ID
    private LocalDateTime concertAt;    // 콘서트 시간
    private int capacity;               // 콘서트 정원

    public static ConcertOption create(Long concertId, LocalDateTime concertAt, int capacity) {
        return ConcertOption.builder()
                .concertId(concertId)
                .concertAt(concertAt)
                .capacity(capacity)
                .build();
    }
}
