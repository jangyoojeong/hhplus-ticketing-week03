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
@Table(name = "user_point_history")
public class UserPointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_point_history_id")
    private Long userPointHistoryId;            // 포인트이력ID (키값)

    @Column(name = "user_id", nullable = false)
    private Long userId;                        // 유저ID

    @Column(name = "amount", nullable = false)
    private Integer amount;                     // 변경금액

    @Column(name = "type", nullable = false)
    private String type;                        // 유형(충전/사용)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;             // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;            // 수정일자

}
