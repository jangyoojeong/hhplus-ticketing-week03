package org.hhplus.ticketing.infra.queue.entity;

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
@Table(name = "queue")
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id")
    private Long queueId;                   // 대기열ID (키값)

    @Column(name = "user_id", nullable = false)
    private Long userId;                    // 유저ID

    @Column(name = "token", nullable = false)
    private String token;                   // 발급된 토큰

    @Column(name = "queue_at", nullable = false)
    private LocalDateTime queueAt;          // 대기진입시간

    @Column(name = "status", nullable = false)
    private String status;                  // 토큰상태 (대기중/입장완료/만료)

    @Column(name = "active_at", nullable = false)
    private LocalDateTime activeAt;         // 토큰활성화시간

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;        // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;        // 수정일자

}
