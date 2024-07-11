package org.hhplus.ticketing.config;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.consert.ConcertRepository;
import org.hhplus.ticketing.domain.consert.model.ConcertDomain;
import org.hhplus.ticketing.domain.consert.model.ConcertOptionDomain;
import org.hhplus.ticketing.domain.consert.model.ConcertSeatDomain;
import org.hhplus.ticketing.domain.consert.model.ReservationDomain;
import org.hhplus.ticketing.domain.consert.model.enums.SeatStatus;
import org.hhplus.ticketing.domain.user.UserInfoRepository;
import org.hhplus.ticketing.domain.user.model.UserInfoDomain;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserInfoRepository userInfoRepository;
    private final ConcertRepository concertRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        List<UserInfoDomain> users = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            users.add(createUserInfoDomain("사용자" + i));
        }

        users.forEach(userInfoRepository::save);

        List<ConcertDomain> concerts = new ArrayList<>();
        concerts.add(createConcert("콘서트1"));

        concerts.forEach(concertRepository::saveConcert);

        List<ConcertOptionDomain> concertOptions = new ArrayList<>();
            concertOptions.add(createConcertOption(1L, LocalDateTime.of(2024, 6, 30, 14, 30, 00), 50));
            concertOptions.add(createConcertOption(1L, LocalDateTime.of(2024, 7, 30, 14, 30, 00), 50));
            concertOptions.add(createConcertOption(1L, LocalDateTime.of(2024, 8, 30, 14, 30, 00), 50));

        concertOptions.forEach(concertRepository::saveConcertOption);

        List<ConcertSeatDomain> concertSeats = new ArrayList<>();

        // 좌석 상태 분배 생성 (좌석 1~50)
        for (int i = 1; i <= 50; i++) {
            SeatStatus status;
            if (i % 3 == 0) {
                status = SeatStatus.AVAILABLE;
            } else if (i % 3 == 1) {
                status = SeatStatus.OCCUPIED;
            } else {
                status = SeatStatus.RESERVED;
            }
            concertSeats.add(createConcertSeat(1L, i, status));
        }

        concertSeats.forEach(concertRepository::saveSeat);

        List<ReservationDomain> reservationDomain = new ArrayList<>();
        concertOptions.add(createConcertOption(1L, LocalDateTime.of(2024, 6, 30, 14, 30, 00), 50));
        concertOptions.add(createConcertOption(1L, LocalDateTime.of(2024, 7, 30, 14, 30, 00), 50));
        concertOptions.add(createConcertOption(1L, LocalDateTime.of(2024, 8, 30, 14, 30, 00), 50));

        concertOptions.forEach(concertRepository::saveConcertOption);
    }

    private UserInfoDomain createUserInfoDomain(String userName) {
        return UserInfoDomain.builder()
                .userName(userName)
                .build();
    }

    private ConcertDomain createConcert(String concertName) {
        return ConcertDomain.builder()
                .concertName(concertName)
                .build();
    }

    private ConcertOptionDomain createConcertOption(Long concertId, LocalDateTime concertAt, int capacity) {
        return ConcertOptionDomain.builder()
                .concertId(concertId)
                .concertAt(concertAt)
                .capacity(capacity)
                .build();
    }

    private ConcertSeatDomain createConcertSeat(Long concertOptionId, int seatNumber, SeatStatus status) {
        return ConcertSeatDomain.builder()
                .concertOptionId(concertOptionId)
                .seatNumber(seatNumber)
                .status(status)
                .build();
    }
}