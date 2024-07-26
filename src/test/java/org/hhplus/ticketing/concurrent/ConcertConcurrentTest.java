package org.hhplus.ticketing.concurrent;

import org.hhplus.ticketing.application.concert.ConcertFacade;
import org.hhplus.ticketing.domain.concert.ConcertRepository;
import org.hhplus.ticketing.domain.concert.model.ConcertCommand;
import org.hhplus.ticketing.domain.concert.model.Reservation;
import org.hhplus.ticketing.utils.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
// @DirtiesContext 컨텍스트의 상태를 초기화
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ConcertConcurrentTest {

    @Autowired
    private ConcertFacade concertFacade;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    TestDataInitializer testDataInitializer;

    Logger log = LoggerFactory.getLogger(ConcertConcurrentTest.class);

    @BeforeEach
    void setUp() {
        testDataInitializer.initializeTestData();
    }

    @Test
    @DisplayName("🔴 좌석_예약_테스트_여러_스레드에서_동시에_좌석_예약시_하나를_제외하고_전부_실패해야한다")
    void concurrentReserveSeatTest_좌석_예약_테스트_여러_스레드에서_동시에_좌석_예약시_하나를_제외하고_전부_실패해야한다22()  {

        // Given
        Long userId = 50L;
        Long concertSeatId1 = 1L;

        // 좌석 예약 요청 command 객체 생성
        ConcertCommand.ReserveSeatCommand command = new ConcertCommand.ReserveSeatCommand(userId, concertSeatId1);

        // 10개의 스레드를 통해 동시에 좌석 예약 시도
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<CompletableFuture<Exception>> futures = new ArrayList<>();

        // 시작 시간 기록
        Instant testStart = Instant.now();
        log.info("테스트 시작 시간 : {}", testStart);

        // When
        // 각 스레드에서 좌석 예약 시도
        for (int i = 0; i < numberOfThreads; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                String currentThreadNm = Thread.currentThread().getName();
                Instant start = Instant.now();
                log.info("{} - 시작 시간 : {}", currentThreadNm, start);
                try {
                    concertFacade.reserveSeat(command);
                    return null;
                } catch (Exception e) {
                    log.error("{} - 예외 발생 : {}", currentThreadNm, e.getMessage());
                    return e;
                } finally {
                    Instant end = Instant.now();
                    log.info("{} - 종료 시간 : {}", currentThreadNm, end);
                    log.info("{} - 경과 시간 : {}", currentThreadNm, Duration.between(start, end).toMillis());
                }
            }, executorService));
        }

        // 모든 작업이 완료되기를 기다림
        List<Exception> exceptions = futures.stream()
                .map(CompletableFuture::join)
                .filter(e -> e != null)
                .collect(Collectors.toList());

        // 종료 시간 기록
        Instant testEnd = Instant.now();
        log.info("테스트 종료 시간 : {}", testEnd);
        log.info("테스트 총 경과 시간 : {} ms", Duration.between(testStart, testEnd).toMillis());

        // Then
        // 예약 성공 결과 확인 (단 하나의 예약만 성공했는지 확인)
        List<Reservation> seatReservations = concertRepository.findByConcertSeatId(concertSeatId1);
        assertThat(seatReservations).hasSize(1);

        // 예외 발생 스레드 개수 체크 (단 하나의 스레드만 성공했는지 검증)
        int numberOfExceptions = exceptions.size();
        assertTrue(numberOfExceptions == numberOfThreads - 1, "예외 발생 스레드 개수 불일치");
    }
}


