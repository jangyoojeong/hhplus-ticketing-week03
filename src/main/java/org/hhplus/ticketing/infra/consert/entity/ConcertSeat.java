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
@Table(name = "concert_seat")
public class ConcertSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_seat_id")
    private Long concertSeatId;             // 콘서트좌석ID

    @Column(name = "concert_option_id", nullable = false)
    private Long concertOptionId;           // 콘서트옵션ID

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;             // 좌석번호 (1~50)

    @Column(name = "status", nullable = false)
    private String status;                  // 좌석상태 (사용가능[Available]/예약됨[Reserved]/점유[Occupied])

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;        // 생성일자

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;        // 수정일자

}