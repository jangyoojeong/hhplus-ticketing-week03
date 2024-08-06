package org.hhplus.ticketing.infra.concert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hhplus.ticketing.domain.concert.model.ConcertSeat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "concert_seat")
public class ConcertSeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_seat_id")
    private Long concertSeatId;             // 콘서트좌석ID

    @Column(name = "concert_option_id", nullable = false)
    private Long concertOptionId;           // 콘서트옵션ID

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;                 // 좌석번호 (1~50)

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private ConcertSeat.Grade grade;        // 좌석등급
    
    @Column(name = "price", nullable = false)
    private int price;                      // 좌석가격
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConcertSeat.Status status;      // 좌석상태 (사용가능[AVAILABLE]/예약됨[RESERVED]/점유[OCCUPIED])

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

    public static ConcertSeatEntity from(ConcertSeat domain) {
        return ConcertSeatEntity.builder()
                .concertSeatId(domain.getConcertSeatId())
                .concertOptionId(domain.getConcertOptionId())
                .seatNumber(domain.getSeatNumber())
                .grade(domain.getGrade())
                .price(domain.getPrice())
                .status(domain.getStatus())
                .version(domain.getVersion())
                .build();
    }

    public ConcertSeat toDomain() {
        return ConcertSeat.builder()
                .concertSeatId(this.getConcertSeatId())
                .concertOptionId(this.getConcertOptionId())
                .seatNumber(this.getSeatNumber())
                .grade(this.getGrade())
                .price(this.getPrice())
                .status(this.getStatus())
                .version(this.getVersion())
                .build();
    }
}