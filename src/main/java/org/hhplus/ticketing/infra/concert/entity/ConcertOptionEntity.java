package org.hhplus.ticketing.infra.concert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.concert.model.ConcertOption;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "concert_option")
public class ConcertOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_option_id")
    private Long concertOptionId;                   // 콘서트옵션ID (키값)

    @Column(name = "concert_id", nullable = false)
    private Long concertId;                         // 콘서트ID

    @Column(name = "concert_at", nullable = false)
    private LocalDateTime concertAt;                // 콘서트 시간

    @Column(name = "capacity", nullable = false)
    private int capacity;                           // 콘서트 정원 (50)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;                // 생성일자

    @Column(nullable = false)
    private LocalDateTime updatedAt;                // 수정일자

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static ConcertOptionEntity from(ConcertOption domain) {
        return ConcertOptionEntity.builder()
                .concertOptionId(domain.getConcertOptionId())
                .concertId(domain.getConcertId())
                .concertAt(domain.getConcertAt())
                .capacity(domain.getCapacity())
                .build();
    }

    public ConcertOption toDomain() {
        return ConcertOption.builder()
                .concertOptionId(this.getConcertOptionId())
                .concertId(this.getConcertId())
                .concertAt(this.getConcertAt())
                .capacity(this.getCapacity())
                .build();
    }
}
