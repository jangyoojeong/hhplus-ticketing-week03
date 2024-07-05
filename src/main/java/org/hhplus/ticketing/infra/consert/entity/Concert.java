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
@Table(name = "consert")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long concertId;                                 // 콘서트ID (키값)

    @Column(name = "concert_name", nullable = false)
    private String concertName;                             // 콘서트명

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;                        // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;                        // 수정일자
}
