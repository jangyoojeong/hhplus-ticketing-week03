package org.hhplus.ticketing.integration;

import org.hhplus.ticketing.application.concert.ConcertFacade;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.concert.ConcertRepository;
import org.hhplus.ticketing.domain.concert.model.*;
import org.hhplus.ticketing.domain.user.model.UserInfo;
import org.hhplus.ticketing.utils.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
// @DirtiesContext 컨텍스트의 상태를 초기화
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ConcertIntegrationTest {

    @Autowired
    private ConcertFacade concertFacade;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    TestDataInitializer testDataInitializer;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private List<UserInfo> savedusers;
    private Concert savedConcert;
    private List<ConcertOption> savedConcertOptions;
    private List<ConcertSeat> savedconcertSeats;

    private Long userId;
    private Long concertId;
    private Long concertOptionId;
    private Long concertSeatId1;
    private Long concertSeatId2;
    private Long concertSeatId3;

    @BeforeEach
    void setUp() {
        // 모든 키 삭제
        redisTemplate.getConnectionFactory().getConnection().flushDb();

        testDataInitializer.initializeTestData();

        // initializer 로 적재된 초기 데이터 세팅
        savedusers = testDataInitializer.getSavedUsers();
        savedConcert = testDataInitializer.getSavedConcert();
        savedConcertOptions = testDataInitializer.getSavedConcertOptions();
        savedconcertSeats = testDataInitializer.getSavedConcertSeats();

        userId = savedusers.get(0).getUserId();
        concertId = savedConcert.getConcertId();
        concertOptionId = savedConcertOptions.get(0).getConcertOptionId();
        concertSeatId1 = savedconcertSeats.get(0).getConcertSeatId();
        concertSeatId2 = savedconcertSeats.get(1).getConcertSeatId();
        concertSeatId3 = savedconcertSeats.get(2).getConcertSeatId();

        // 예약정보 초기데이터 적재
        // 만료대상 X
        Reservation reservation1 = Reservation.builder()
                .concertSeatId(concertSeatId2)
                .userId(userId)
                .reservationAt(LocalDateTime.now())
                .status(Reservation.Status.RESERVED)
                .build();
        concertRepository.saveReservation(reservation1);

        // 만료대상 O
        Reservation reservation2 = Reservation.builder()
                .concertSeatId(concertSeatId3)
                .userId(userId)
                .reservationAt(LocalDateTime.now().minusMinutes(6))
                .status(Reservation.Status.RESERVED)
                .build();
        concertRepository.saveReservation(reservation2);

        ConcertSeat concertSeat1 = savedconcertSeats.get(1);
        concertSeat1.setReserved();
        concertRepository.saveSeat(concertSeat1);

        ConcertSeat concertSeat2 = savedconcertSeats.get(2);
        concertSeat2.setReserved();
        concertRepository.saveSeat(concertSeat2);
    }

    @Test
    @DisplayName("🟢 콘서트_목록_조회_테스트_적재된_1건의_데이터가_리턴된다")
    void getConcertListTest_콘서트_목록_조회_테스트_적재된_1건의_데이터가_리턴된다() {

        // Given
        List<Concert> concertList = Arrays.asList(
                new Concert(1L, "콘서트1")
        );

        Pageable pageable = PageRequest.of(0, 20);

        Page<Concert> concerts = new PageImpl<>(concertList, pageable, concertList.size());

        List<ConcertResult.GetConcertList> result = concerts.stream()
                .map(ConcertResult.GetConcertList::from)
                .collect(Collectors.toList());
        Page<ConcertResult.GetConcertList> expectedResult = new PageImpl<>(result, pageable, result.size());

        // When
        Page<ConcertResult.GetConcertList> actualResult = concertFacade.getConcertList(pageable);

        // Then
        assertNotNull(actualResult);
        assertThat(actualResult.getContent()).isEqualTo(expectedResult.getContent());
    }

    @Test
    @DisplayName("🟢 콘서트_등록_테스트_저장후_저장된_콘서트_정보가_리턴된다")
    void saveConcertTest_콘서트_등록_테스트_저장후_저장된_콘서트_정보가_리턴된다() {

        // Given
        String concertName = "콘서트1";

        // When
        ConcertResult.SaveConcert retult = concertFacade.saveConcert(new ConcertCommand.SaveConcert("콘서트1"));

        assertThat(retult.getConcertName()).isEqualTo(concertName);
    }

    @Test
    @DisplayName("🟢 콘서트_목록_조회_테스트_두번째_조회시에는_캐시에_있는_데이터를_참조한다")
    public void getConcertListTest_콘서트_목록_조회_테스트_두번째_조회시에는_캐시에_있는_데이터를_참조한다() {

        // Given
        Pageable pageable = PageRequest.of(0, 20);

        // When
        // 첫 번째 호출
        Page<ConcertResult.GetConcertList> firstCall = concertFacade.getConcertList(pageable);
        assertThat(firstCall).isNotNull();

        // 캐시 생성 확인
        String cacheKey = "concertCache::" + 0;
        assertThat(redisTemplate.hasKey(cacheKey)).isTrue();

        // 두 번째 호출 (캐시 적용 확인)
        Page<ConcertResult.GetConcertList> secondCall = concertFacade.getConcertList(pageable);
        assertThat(secondCall).isNotNull();

        // 캐시 TTL 확인
        Long ttl = redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
        assertThat(ttl).isGreaterThan(0);
    }

    @Test
    @DisplayName("🟢 콘서트_옵션_등록_테스트_저장후_저장된_콘서트_옵션_정보가_리턴된다")
    void saveConcertOptionTest_콘서트_옵션_등록_테스트_저장후_저장된_콘서트_옵션_정보가_리턴된다() {

        // Given
        Long concertId = 1L;
        LocalDateTime concertAt = LocalDateTime.now().plusDays(1);
        int capacity = 50;

        ConcertCommand.SaveConcertOption command = new ConcertCommand.SaveConcertOption(concertId, concertAt, capacity);

        // When
        ConcertResult.SaveConcertOption retult = concertFacade.saveConcertOption(command);

        assertThat(retult.getConcertId()).isEqualTo(concertId);
        assertThat(retult.getConcertAt()).isEqualTo(concertAt);
        assertThat(retult.getCapacity()).isEqualTo(capacity);
    }

    @Test
    @DisplayName("🟢 예약_가능한_날짜_조회_테스트_두번째_조회시에는_캐시에_있는_데이터를_참조한다")
    public void getDatesForReservationTest_예약_가능한_날짜_조회_테스트_두번째_조회시에는_캐시에_있는_데이터를_참조한다() {

        // When
        // 첫 번째 호출
        ConcertResult.GetAvailableDates firstCall = concertFacade.getAvailableDates(concertId);
        assertThat(firstCall).isNotNull();

        // 캐시 생성 확인
        String cacheKey = "concertOptionCache::" + concertId;
        assertThat(redisTemplate.hasKey(cacheKey)).isTrue();

        // When 두 번째 호출 (캐시 적용 확인)
        ConcertResult.GetAvailableDates secondCall = concertFacade.getAvailableDates(concertId);
        assertThat(secondCall).isNotNull();

        // 캐시 TTL 확인
        Long ttl = redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
        assertThat(ttl).isGreaterThan(0);
    }

    @Test
    @DisplayName("🟢 예약_가능한_날짜_조회_테스트_적재된_2건의_데이터에서_필터링된_리스트와_리턴된_리스트가_일치한다")
    void getDatesForReservationTest_예약_가능한_날짜_조회_테스트_적재된_2건의_데이터에서_필터링된_리스트와_리턴된_리스트가_일치한다() {

        // Given
        // 초기 적재 데이터에서 조회할 concertId 의 예약가능한상태(콘서트일자가 현재일 이후) 콘서트옵션리스트 세팅
        List<ConcertOption> availableConcertDates = savedConcertOptions.stream()
                .filter(option -> option.getConcertId().equals(concertId))
                .filter(option -> option.getConcertAt().isAfter(LocalDateTime.now())) // 현재 날짜 이후 필터링
                .collect(Collectors.toList());

        ConcertResult.GetAvailableDates expectedResult = ConcertResult.GetAvailableDates.from(availableConcertDates);

        // When
        ConcertResult.GetAvailableDates actualResult = concertFacade.getAvailableDates(concertId);

        // Then
        assertNotNull(actualResult);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("🟢 예약_가능한_좌석_조회_테스트_적재된_좌석_데이터에서_필터링된_리스트와_리턴된_리스트가_일치한다")
    void getSeatsForReservationTest_예약_가능한_좌석_조회_테스트_적재된_좌석_데이터에서_필터링된_리스트와_리턴된_리스트가_일치한다() {

        // Given
        // 초기 적재 데이터에서 조회할 concertOptionId 의 AVAILABLE 상태의 좌석리스트 세팅
        List<ConcertSeat> availableConcertSeats = savedconcertSeats.stream()
                .filter(seat -> seat.getConcertOptionId().equals(concertOptionId))
                .filter(seat -> seat.getStatus() == ConcertSeat.Status.AVAILABLE)
                .collect(Collectors.toList());

        ConcertResult.GetAvailableSeats expectedResult = ConcertResult.GetAvailableSeats.from(availableConcertSeats);

        // When
        ConcertResult.GetAvailableSeats actualResult = concertFacade.getAvailableSeats(concertOptionId);

        // Then
        assertNotNull(actualResult);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("🟢 좌석_예약_테스트_좌석_예약_성공시_예약된_정보가_반환된다")
    void reserveSeatTest_좌석_예약_테스트_좌석_예약_성공시_예약된_정보가_반환된다() {
        // Given
        ConcertCommand.ReserveSeat command = new ConcertCommand.ReserveSeat(userId, concertSeatId1);

        // When
        ConcertResult.ReserveSeat actualResult = concertFacade.reserveSeat(command);

        // Then
        List<Reservation> userReservations = concertRepository.findByUserId(userId);
        assertNotNull(actualResult);
        // 기존 2건에 1건이 추가된 3건이 리턴된다
        assertThat(userReservations).hasSize(3);
        assertThat(actualResult.getConcertSeatId()).isEqualTo(concertSeatId1);
    }

    @Test
    @DisplayName("🔴 좌석_예약_테스트_해당_좌석이_예약가능한_상태가_아닐_경우_SEAT_NOT_FOUND_예외반환")
    void reserveSeatTest_좌석_예약_테스트_해당_좌석이_예약가능한_상태가_아닐_경우_SEAT_NOT_FOUND_예외반환() {
        // Given
        ConcertCommand.ReserveSeat command = new ConcertCommand.ReserveSeat(userId, concertSeatId1);

        // 좌석을 미리 예약
        concertFacade.reserveSeat(command);

        // When & Then
        assertThatThrownBy(() -> concertFacade.reserveSeat(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SEAT_NOT_FOUND_OR_ALREADY_RESERVED);
    }

    @Test
    @DisplayName("🟢 임시_예약_만료_처리_테스트_총_2건_중_만료대상_1건이_만료된다")
    void releaseTemporaryReservationsTest_임시_예약_만료_처리_테스트_총_2건_중_만료대상_1건이_만료된다() {

        // Given
        // When
        concertFacade.releaseReservations();

        List<Reservation> expiredReservations = concertRepository.getExpiredReservations(LocalDateTime.now().minusMinutes(5));
        assertThat(expiredReservations).hasSize(0);
    }

    @Test
    @DisplayName("🟢 만료_예약_좌석_상태_갱신_테스트_만료된_1건의_좌석상태가_사용가능으로_갱신된다")
    void releaseTemporaryReservationsTest_만료_예약_좌석_상태_갱신_테스트_만료된_1건의_좌석상태가_사용가능으로_갱신된다() {

        // Given
        // When
        concertFacade.releaseReservations();

        Optional<ConcertSeat> seatInfo1 = concertRepository.findSeatById(concertSeatId2);
        Optional<ConcertSeat> seatInfo2 = concertRepository.findSeatById(concertSeatId3);

        assertThat(seatInfo1.get().getStatus()).isEqualTo(ConcertSeat.Status.RESERVED);
        assertThat(seatInfo2.get().getStatus()).isEqualTo(ConcertSeat.Status.AVAILABLE);
    }
}
