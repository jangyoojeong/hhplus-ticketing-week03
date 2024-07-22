package org.hhplus.ticketing.support.config;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.concert.ConcertRepository;
import org.hhplus.ticketing.domain.concert.model.Concert;
import org.hhplus.ticketing.domain.concert.model.ConcertOption;
import org.hhplus.ticketing.domain.concert.model.ConcertSeat;
import org.hhplus.ticketing.domain.user.UserInfoRepository;
import org.hhplus.ticketing.domain.user.model.UserInfo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserInfoRepository userInfoRepository;
    private final ConcertRepository concertRepository;

    private List<UserInfo> savedusers;
    private Concert savedConcert;
    private List<ConcertOption> savedConcertOptions;
    private List<ConcertSeat> savedconcertSeats;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void run(String... args) throws Exception {

        // User Dummy 데이터 생성
        List<UserInfo> users = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            users.add(UserInfo.builder()
                    .userName("사용자" + i)
                    .build());
        }

        savedusers = users.stream()
                .map(userInfoRepository::save)
                .collect(Collectors.toList());

        // Concert Dummy 데이터 생성
        Concert saveConcert = Concert.create("콘서트1");

        savedConcert = concertRepository.saveConcert(saveConcert);

        // ConcertOption Dummy 데이터 생성
        List<ConcertOption> concertOptions = Arrays.asList(
                ConcertOption.create(savedConcert.getConcertId(), LocalDateTime.now().plusDays(15), 50),    // 현시점 기준 15일 후
                ConcertOption.create(savedConcert.getConcertId(), LocalDateTime.now().minusDays(15), 30)    // 현시점 기준 15일 전
        );

        savedConcertOptions = concertOptions.stream()
                .map(concertRepository::saveConcertOption)
                .collect(Collectors.toList());

        // ConcertSeat Dummy 데이터 생성
        List<ConcertSeat> concertSeats = new ArrayList<>();

        for (ConcertOption option : savedConcertOptions) {
            int capacity = option.getCapacity();
            int vipCount = (int) (capacity * 0.1);       // VIP 등급 10%
            int premiumCount = (int) (capacity * 0.2);   // PREMIUM 등급 20%
            int regularCount = (int) (capacity * 0.4);   // REGULAR 등급 40%

            for (int i = 1; i <= option.getCapacity(); i++) {
                ConcertSeat.Grade grade;

                if (i <= vipCount) {
                    grade = ConcertSeat.Grade.VIP;
                } else if (i <= vipCount + premiumCount) {
                    grade = ConcertSeat.Grade.PREMIUM;
                } else if (i <= vipCount + premiumCount + regularCount) {
                    grade = ConcertSeat.Grade.REGULAR;
                } else {
                    grade = ConcertSeat.Grade.ECONOMY;
                }

                concertSeats.add(ConcertSeat.create(option.getConcertOptionId(), i, grade));
            }
        }

        savedconcertSeats = concertSeats.stream()
                .map(concertRepository::saveSeat)
                .collect(Collectors.toList());
    }
}