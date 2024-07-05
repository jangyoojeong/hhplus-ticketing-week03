package org.hhplus.ticketing.infra.consert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "concert_option")
public class ConcertOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_option_id")
    private Long concertOptionId;                   // 콘서트옵션ID (키값)

    @Column(name = "concert_id", nullable = false)
    private Long concertId;                         // 콘서트ID

    @Column(name = "concert_at", nullable = false)
    private LocalDateTime concertAt;                // 콘서트 시간

    @Column(name = "capacity", nullable = false)
    private Integer capacity;                       // 콘서트 정원 (50)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;                // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;                // 수정일자

}
