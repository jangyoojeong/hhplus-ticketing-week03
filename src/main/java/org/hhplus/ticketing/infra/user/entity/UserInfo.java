package org.hhplus.ticketing.infra.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "user_id")
    private Long userId;                // 유저ID (키값)

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;                  // uuid

    @Column(name = "user_name", nullable = false)
    private String userName;            // 유저 이름

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;    // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;    // 수정일자

}
