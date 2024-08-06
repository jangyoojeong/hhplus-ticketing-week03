package org.hhplus.ticketing.infra.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.user.model.UserInfo;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_info")
public class UserInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;                      // 유저ID (키값)

    @Column(name = "user_name", nullable = false)
    private String userName;                 // 유저 이름

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;         // 생성일자

    @Column(nullable = false)
    private LocalDateTime updatedAt;         // 수정일자

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static UserInfoEntity from(UserInfo domain) {
        return UserInfoEntity.builder()
                .userId(domain.getUserId())
                .userName(domain.getUserName())
                .build();
    }

    public UserInfo toDomain() {
        return UserInfo.builder()
                .userId(this.getUserId())
                .userName(this.getUserName())
                .build();
    }
}
