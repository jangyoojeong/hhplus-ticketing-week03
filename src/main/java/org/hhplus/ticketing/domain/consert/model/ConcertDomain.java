package org.hhplus.ticketing.domain.consert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 도메인 객체: Concert
 * 콘서트에 대한 기본 정보를 관리합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertDomain {
    private Long concertId;        // 콘서트ID (키값)
    private String concertName;    // 콘서트명
}