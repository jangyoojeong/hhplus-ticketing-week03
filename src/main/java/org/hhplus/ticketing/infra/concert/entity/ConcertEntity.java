package org.hhplus.ticketing.infra.concert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.concert.model.Concert;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "concert")
public class ConcertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long concertId;                                 // 콘서트ID (키값)

    @Column(name = "concert_name", nullable = false)
    private String concertName;                             // 콘서트명

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;                        // 생성일자

    @Column(nullable = false)
    private LocalDateTime updatedAt;                        // 수정일자

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static ConcertEntity from(Concert domain) {
        return ConcertEntity.builder()
                .concertId(domain.getConcertId())
                .concertName(domain.getConcertName())
                .build();
    }

    public Concert toDomain() {
        return Concert.builder()
                .concertId(this.getConcertId())
                .concertName(this.getConcertName())
                .build();
    }
}
