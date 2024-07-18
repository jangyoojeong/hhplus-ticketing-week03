package org.hhplus.ticketing.integration;

import org.hhplus.ticketing.application.concert.facade.ConcertFacade;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.concert.ConcertRepository;
import org.hhplus.ticketing.domain.concert.model.*;
import org.hhplus.ticketing.domain.concert.model.enums.ReservationStatus;
import org.hhplus.ticketing.domain.concert.model.enums.SeatStatus;
import org.hhplus.ticketing.domain.queue.model.QueueCommand;
import org.hhplus.ticketing.domain.user.model.UserInfoDomain;
import org.hhplus.ticketing.utils.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private List<UserInfoDomain> savedusers;
    private ConcertDomain savedConcert;
    private List<ConcertOptionDomain> savedConcertOptions;
    private List<ConcertSeatDomain> savedconcertSeats;

    private Long userId;
    private Long concertId;
    private Long concertOptionId;
    private Long concertSeatId1;
    private Long concertSeatId2;
    private Long concertSeatId3;

    @BeforeEach
    void setUp() {
        testDataInitializer.initializeTestData();

        // initializer 로 적재된 초기 데이터 세팅
        savedusers = testDataInitializer.getSavedusers();
        savedConcert = testDataInitializer.getSavedConcert();
        savedConcertOptions = testDataInitializer.getSavedConcertOptions();
        savedconcertSeats = testDataInitializer.getSavedconcertSeats();

        userId = savedusers.get(0).getUserId();
        concertId = savedConcert.getConcertId();
        concertOptionId = savedConcertOptions.get(0).getConcertOptionId();
        concertSeatId1 = savedconcertSeats.get(0).getConcertSeatId();
        concertSeatId2 = savedconcertSeats.get(1).getConcertSeatId();
        concertSeatId3 = savedconcertSeats.get(2).getConcertSeatId();

        // 예약정보 초기데이터 적재
        // 만료대상 X
        ReservationDomain reservation1 = ReservationDomain.builder()
                .concertSeatId(concertSeatId2)
                .userId(userId)
                .reservationAt(LocalDateTime.now())
                .status(ReservationStatus.RESERVED)
                .build();
        concertRepository.saveReservation(reservation1);

        // 만료대상 O
        ReservationDomain reservation2 = ReservationDomain.builder()
                .concertSeatId(concertSeatId3)
                .userId(userId)
                .reservationAt(LocalDateTime.now().minusMinutes(6))
                .status(ReservationStatus.RESERVED)
                .build();
        concertRepository.saveReservation(reservation2);

        ConcertSeatDomain concertSeat1 = savedconcertSeats.get(1);
        concertSeat1.updateSeatReserved();
        concertRepository.saveSeat(concertSeat1);

        ConcertSeatDomain concertSeat2 = savedconcertSeats.get(2);
        concertSeat2.updateSeatReserved();
        concertRepository.saveSeat(concertSeat2);
    }

    @Test
    @DisplayName("[성공테스트] 예약_가능한_날짜_조회_테스트_적재된_2건의_데이터에서_필터링된_리스트와_리턴된_리스트가_일치한다")
    void getDatesForReservationTest_예약_가능한_날짜_조회_테스트_적재된_2건의_데이터에서_필터링된_리스트와_리턴된_리스트가_일치한다() {

        // Given
        // 초기 적재 데이터에서 조회할 concertId 의 예약가능한상태(콘서트일자가 현재일 이후) 콘서트옵션리스트 세팅
        List<ConcertOptionDomain> availableConcertDates = savedConcertOptions.stream()
                .filter(option -> option.getConcertId().equals(concertId))
                .filter(option -> option.getConcertAt().isAfter(LocalDateTime.now())) // 현재 날짜 이후 필터링
                .collect(Collectors.toList());

        ConcertResult.DatesForReservationResult expectedResult = ConcertResult.DatesForReservationResult.from(availableConcertDates);

        // When
        ConcertResult.DatesForReservationResult actualResult = concertFacade.getDatesForReservation(concertId);

        // Then
        assertNotNull(actualResult);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("[성공테스트] 예약_가능한_좌석_조회_테스트_적재된_좌석_데이터에서_필터링된_리스트와_리턴된_리스트가_일치한다")
    void getSeatsForReservationTest_예약_가능한_좌석_조회_테스트_적재된_좌석_데이터에서_필터링된_리스트와_리턴된_리스트가_일치한다() {

        // Given
        // 초기 적재 데이터에서 조회할 concertOptionId 의 AVAILABLE 상태의 좌석리스트 세팅
        List<ConcertSeatDomain> availableConcertSeats = savedconcertSeats.stream()
                .filter(seat -> seat.getConcertOptionId().equals(concertOptionId))
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                .collect(Collectors.toList());

        ConcertResult.SeatsForReservationResult expectedResult = ConcertResult.SeatsForReservationResult.from(availableConcertSeats);

        // When
        ConcertResult.SeatsForReservationResult actualResult = concertFacade.getSeatsForReservation(concertOptionId);

        // Then
        assertNotNull(actualResult);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("[성공테스트] 좌석_예약_테스트_좌석_예약_성공시_예약된_정보가_반환된다")
    void reserveSeatTest_좌석_예약_테스트_좌석_예약_성공시_예약된_정보가_반환된다() {
        // Given
        ConcertCommand.ReserveSeatCommand command = new ConcertCommand.ReserveSeatCommand(userId, concertSeatId1);

        // When
        ConcertResult.ReserveSeatResult actualResult = concertFacade.reserveSeat(command);

        // Then
        List<ReservationDomain> userReservations = concertRepository.findByUserId(userId);
        assertNotNull(actualResult);
        // 기존 2건에 1건이 추가된 3건이 리턴된다
        assertThat(userReservations).hasSize(3);
        assertThat(actualResult.getConcertSeatId()).isEqualTo(concertSeatId1);
    }

    @Test
    @DisplayName("[실패테스트] 좌석_예약_테스트_해당_좌석이_예약가능한_상태가_아닐_경우_SEAT_NOT_FOUND_예외반환")
    void reserveSeatTest_좌석_예약_테스트_해당_좌석이_예약가능한_상태가_아닐_경우_SEAT_NOT_FOUND_예외반환() {
        // Given
        ConcertCommand.ReserveSeatCommand command = new ConcertCommand.ReserveSeatCommand(userId, concertSeatId1);

        // 좌석을 미리 예약
        concertFacade.reserveSeat(command);

        // When & Then
        assertThatThrownBy(() -> concertFacade.reserveSeat(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SEAT_NOT_FOUND);
    }

    @Test
    @DisplayName("[실패테스트] 좌석_예약_테스트_여러_스레드에서_동시에_좌석_예약시_하나를_제외하고_전부_실패해야한다")
    void concurrentReserveSeatTest_좌석_예약_테스트_여러_스레드에서_동시에_좌석_예약시_하나를_제외하고_전부_실패해야한다() throws InterruptedException, ExecutionException {

        // Given
        Long userId = 50L;

        // 좌석 예약 요청 command 객체 생성
        ConcertCommand.ReserveSeatCommand command = new ConcertCommand.ReserveSeatCommand(userId, concertSeatId1);

        // 10개의 스레드를 통해 동시에 좌석 예약 시도
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<Exception>> futures = new ArrayList<>();

        // When
        // 각 스레드에서 좌석 예약 시도
        for (int i = 0; i < numberOfThreads; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    concertFacade.reserveSeat(command);
                    return null;
                } catch (Exception e) {
                    return e;
                }
            }));
        }

        // Then
        // 예외 결과 확인
        List<Exception> exceptions = new ArrayList<>();
        for (Future<Exception> future : futures) {
            Exception e = future.get();
            if (e != null) {
                exceptions.add(e);
            }
        }

        // 예약 성공 결과 확인 (단 하나의 예약만 성공했는지 확인)
        List<ReservationDomain> userReservations = concertRepository.findByUserId(userId);
        assertThat(userReservations).hasSize(1);

        // 예외 발생 스레드 개수 체크 (단 하나의 스레드만 성공했는지 검증)
        int numberOfExceptions = exceptions.size();
        assertTrue(numberOfExceptions == numberOfThreads - 1, "예외 발생 스레드 개수 불일치");
    }

    @Test
    @DisplayName("[성공테스트] 임시_예약_만료_처리_테스트_총_2건_중_만료대상_1건이_만료된다")
    void releaseTemporaryReservationsTest_임시_예약_만료_처리_테스트_총_2건_중_만료대상_1건이_만료된다() {

        // Given
        // When
        concertFacade.releaseTemporaryReservations();

        List<ReservationDomain> expiredReservations = concertRepository.findReservedBefore(LocalDateTime.now().minusMinutes(5));
        assertThat(expiredReservations).hasSize(0);
    }

    @Test
    @DisplayName("[성공테스트] 만료_예약_좌석_상태_갱신_테스트_만료된_1건의_좌석상태가_사용가능으로_갱신된다")
    void releaseTemporaryReservationsTest_만료_예약_좌석_상태_갱신_테스트_만료된_1건의_좌석상태가_사용가능으로_갱신된다() {

        // Given
        // When
        concertFacade.releaseTemporaryReservations();

        Optional<ConcertSeatDomain> seatInfo1 = concertRepository.findSeatById(concertSeatId2);
        Optional<ConcertSeatDomain> seatInfo2 = concertRepository.findSeatById(concertSeatId3);

        assertThat(seatInfo1.get().getStatus()).isEqualTo(SeatStatus.RESERVED);
        assertThat(seatInfo2.get().getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }
}
