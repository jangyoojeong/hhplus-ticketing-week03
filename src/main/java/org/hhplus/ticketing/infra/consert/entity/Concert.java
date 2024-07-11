package org.hhplus.ticketing.infra.consert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.consert.model.ConcertDomain;
import org.hhplus.ticketing.domain.user.model.UserPointDomain;
import org.hhplus.ticketing.infra.user.entity.UserPoint;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "consert")
public class Concert {

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

    public static Concert from(ConcertDomain domain) {
        return Concert.builder()
                .concertId(domain.getConcertId())
                .concertName(domain.getConcertName())
                .build();
    }

    public ConcertDomain toDomain() {
        return ConcertDomain.builder()
                .concertId(this.getConcertId())
                .concertName(this.getConcertName())
                .build();
    }
}
