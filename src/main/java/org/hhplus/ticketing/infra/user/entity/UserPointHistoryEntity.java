package org.hhplus.ticketing.infra.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.user.model.UserPointHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_point_history")
public class UserPointHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_point_history_id")
    private Long userPointHistoryId;            // 포인트이력ID (키값)

    @Column(name = "user_id", nullable = false)
    private Long userId;                        // 유저ID

    @Column(name = "amount", nullable = false)
    private Integer amount;                     // 변경금액

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false)
    private UserPointHistory.Type type;         // 유형(CHARGE/USE)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;            // 생성일자

    @Column(nullable = false)
    private LocalDateTime updatedAt;            // 수정일자

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static UserPointHistoryEntity from(UserPointHistory domain) {
        return UserPointHistoryEntity.builder()
                .userPointHistoryId(domain.getUserPointHistoryId())
                .userId(domain.getUserId())
                .amount(domain.getAmount())
                .type(domain.getType())
                .build();
    }

    public static UserPointHistory toDomain(UserPointHistoryEntity entity) {
        return UserPointHistory.builder()
                .userPointHistoryId(entity.userPointHistoryId)
                .userId(entity.userId)
                .amount(entity.amount)
                .type(entity.type)
                .build();
    }

    public static List<UserPointHistory> toDomainList(List<UserPointHistoryEntity> entityList) {
        return entityList.stream()
                .map(UserPointHistoryEntity::toDomain)
                .collect(Collectors.toList());
    }
}
