package org.hhplus.ticketing.utils;

import org.hhplus.ticketing.domain.concert.ConcertRepository;
import org.hhplus.ticketing.domain.concert.model.ConcertDomain;
import org.hhplus.ticketing.domain.concert.model.ConcertOptionDomain;
import org.hhplus.ticketing.domain.concert.model.ConcertSeatDomain;
import org.hhplus.ticketing.domain.concert.model.enums.SeatStatus;
import org.hhplus.ticketing.domain.user.UserInfoRepository;
import org.hhplus.ticketing.domain.user.model.UserInfoDomain;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class TestDataInitializer {

    private final UserInfoRepository userInfoRepository;
    private final ConcertRepository concertRepository;

    public TestDataInitializer(UserInfoRepository userInfoRepository, ConcertRepository concertRepository) {
        this.userInfoRepository = userInfoRepository;
        this.concertRepository = concertRepository;
    }

    private List<UserInfoDomain> savedusers;
    private ConcertDomain savedConcert;
    private List<ConcertOptionDomain> savedConcertOptions;
    private List<ConcertSeatDomain> savedconcertSeats;

    public void initializeTestData() {

        // User Dummy 데이터 생성
        List<UserInfoDomain> users = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            users.add(UserInfoDomain.builder()
                .userName("사용자" + i)
                .build());
        }

        savedusers = users.stream()
                .map(userInfoRepository::save)
                .collect(Collectors.toList());

        // Concert Dummy 데이터 생성
        ConcertDomain saveConcert = new ConcertDomain("콘서트1");

        savedConcert = concertRepository.saveConcert(saveConcert);

        // ConcertOption Dummy 데이터 생성
        List<ConcertOptionDomain> concertOptions = new ArrayList<>();

        concertOptions.add(ConcertOptionDomain.builder()
                .concertId(savedConcert.getConcertId())
                .concertAt(LocalDateTime.now().plusDays(15))    // 현시점 기준 15일 후
                .capacity(50)
                .build());

        concertOptions.add(ConcertOptionDomain.builder()
                .concertId(savedConcert.getConcertId())
                .concertAt(LocalDateTime.now().minusDays(15))    // 현시점 기준 15일 전
                .capacity(30)
                .build());

        savedConcertOptions = concertOptions.stream()
                .map(concertRepository::saveConcertOption)
                .collect(Collectors.toList());

        // ConcertSeat Dummy 데이터 생성
        List<ConcertSeatDomain> concertSeats = new ArrayList<>();

        for (ConcertOptionDomain option : savedConcertOptions) {
            for (int i = 1; i <= option.getCapacity(); i++) {
                SeatStatus status = SeatStatus.AVAILABLE;
                concertSeats.add(ConcertSeatDomain.builder()
                        .concertOptionId(option.getConcertOptionId())
                        .seatNumber(i)
                        .status(status)
                        .build());
            }
        }

        savedconcertSeats = concertSeats.stream()
                .map(concertRepository::saveSeat)
                .collect(Collectors.toList());
    }

    public List<UserInfoDomain> getSavedusers() {
        return savedusers;
    }

    public ConcertDomain getSavedConcert() {
        return savedConcert;
    }

    public List<ConcertOptionDomain> getSavedConcertOptions() {
        return savedConcertOptions;
    }

    public List<ConcertSeatDomain> getSavedconcertSeats() {
        return savedconcertSeats;
    }
}