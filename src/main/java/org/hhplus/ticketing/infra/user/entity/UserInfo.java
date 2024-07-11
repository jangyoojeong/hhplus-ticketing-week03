package org.hhplus.ticketing.infra.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.user.model.UserInfoDomain;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;                      // 유저ID (키값)

    @Column(name = "uuid", nullable = false, unique = true, updatable = false, columnDefinition = "BINARY(16)")
    private UUID uuid;                       // 유저 uuid

    @Column(name = "user_name", nullable = false)
    private String userName;                 // 유저 이름

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;         // 생성일자

    @Column(nullable = false)
    private LocalDateTime updatedAt;         // 수정일자

    @PrePersist
    private void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static UserInfo from(UserInfoDomain domain) {
        return UserInfo.builder()
                .userId(domain.getUserId())
                .uuid(domain.getUuid())
                .userName(domain.getUserName())
                .build();
    }

    public UserInfoDomain toDomain() {
        return UserInfoDomain.builder()
                .userId(this.getUserId())
                .uuid(this.getUuid())
                .userName(this.getUserName())
                .build();
    }
}
