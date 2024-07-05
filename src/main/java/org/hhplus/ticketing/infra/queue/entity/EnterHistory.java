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
@Table(name = "enter_history")
public class EnterHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enter_history_id")
    private Long enterHistoryId;            // 입장이력ID (키값)

    @Column(name = "queue_id", nullable = false)
    private Long queueId;                   // 대기열ID

    @Column(name = "entered_at", nullable = false)
    private LocalDateTime enteredAt;        // 입장시간

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;        // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;        // 수정일자
}
