package org.hhplus.ticketing.infra.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.user.model.UserPoint;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_point")
public class UserPointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_point_id")
    private Long userPointId;               // 포인트ID (키값)

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;                    // 유저ID

    @Column(name = "point", nullable = false)
    private int point;                      // 포인트

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;        // 생성일자

    @Column(nullable = false)
    private LocalDateTime updatedAt;        // 수정일자

    @Version
    @Column(name = "version")
    private Long version;                   // 낙관적 락 버전 필드

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static UserPointEntity from(UserPoint domain) {
        return UserPointEntity.builder()
                .userPointId(domain.getUserPointId())
                .userId(domain.getUserId())
                .point(domain.getPoint())
                .version(domain.getVersion())
                .build();
    }

    public UserPoint toDomain() {
        return UserPoint.builder()
                .userPointId(this.getUserPointId())
                .userId(this.getUserId())
                .point(this.getPoint())
                .version(this.getVersion())
                .build();
    }
}
