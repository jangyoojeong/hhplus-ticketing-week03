package org.hhplus.ticketing.infra.user.entity;

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
@Table(name = "user_point")
public class UserPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_point_id")
    private Long userPointId;               // 포인트ID (키값)

    @Column(name = "user_id", nullable = false)
    private Long userId;                    // 유저ID

    @Column(name = "point", nullable = false)
    private Integer point;                  // 포인트

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;        // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;        // 수정일자
}
