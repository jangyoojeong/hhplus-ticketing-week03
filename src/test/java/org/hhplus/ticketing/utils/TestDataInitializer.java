package org.hhplus.ticketing.utils;

import lombok.Getter;
import org.hhplus.ticketing.domain.concert.ConcertRepository;
import org.hhplus.ticketing.domain.concert.model.Concert;
import org.hhplus.ticketing.domain.concert.model.ConcertOption;
import org.hhplus.ticketing.domain.concert.model.ConcertSeat;
import org.hhplus.ticketing.domain.user.UserInfoRepository;
import org.hhplus.ticketing.domain.user.UserPointRepository;
import org.hhplus.ticketing.domain.user.model.UserInfo;
import org.hhplus.ticketing.domain.user.model.UserPoint;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Component
public class TestDataInitializer {

    private final UserInfoRepository userInfoRepository;
    private final UserPointRepository userPointRepository;
    private final ConcertRepository concertRepository;

    public TestDataInitializer(UserInfoRepository userInfoRepository, UserPointRepository userPointRepository, ConcertRepository concertRepository) {
        this.userInfoRepository = userInfoRepository;
        this.userPointRepository = userPointRepository;
        this.concertRepository = concertRepository;
    }

    private List<UserInfo> savedUsers;
    private Concert savedConcert;
    private List<ConcertOption> savedConcertOptions;
    private List<ConcertSeat> savedConcertSeats;

    public void initializeTestData() {
        // User Dummy 데이터 생성
        savedUsers = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            savedUsers.add(UserInfo.builder()
                    .userName("사용자" + i)
                    .build());
        }

        // UserInfo 데이터를 저장하고, 동시에 UserPoint 데이터도 저장
        savedUsers = savedUsers.stream()
                .map(userInfo -> {
                    UserInfo savedUserInfo = userInfoRepository.save(userInfo);
                    UserPoint userPoint = UserPoint.builder().userId(savedUserInfo.getUserId()).point(0).build();
                    userPointRepository.save(userPoint);
                    return savedUserInfo;
                })
                .collect(Collectors.toList());

        // Concert Dummy 데이터 생성
        Concert concert = Concert.create("콘서트1");
        savedConcert = concertRepository.saveConcert(concert);

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

        savedConcertSeats = concertSeats.stream()
                .map(concertRepository::saveSeat)
                .collect(Collectors.toList());
    }

}