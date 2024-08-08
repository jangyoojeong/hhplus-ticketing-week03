package org.hhplus.ticketing.concurrent;

import org.hhplus.ticketing.application.payment.PaymentCreteria;
import org.hhplus.ticketing.application.payment.PaymentFacade;
import org.hhplus.ticketing.application.user.UserFacade;
import org.hhplus.ticketing.domain.concert.ConcertRepository;
import org.hhplus.ticketing.domain.concert.model.ConcertSeat;
import org.hhplus.ticketing.domain.concert.model.Reservation;
import org.hhplus.ticketing.domain.payment.PaymentRepository;
import org.hhplus.ticketing.domain.payment.model.Payment;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.queue.QueueRepository;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.domain.user.UserPointRepository;
import org.hhplus.ticketing.domain.user.UserPointService;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserInfo;
import org.hhplus.ticketing.domain.user.model.UserPoint;
import org.hhplus.ticketing.utils.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
// @DirtiesContext 컨텍스트의 상태를 초기화
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaymentConcurrentTest {

    Logger log = LoggerFactory.getLogger(PaymentConcurrentTest.class);

    @Autowired
    private PaymentFacade paymentFacade;
    @Autowired
    private UserFacade userFacade;
    @Autowired
    private UserPointService userPointService;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private UserPointRepository userPointRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    TestDataInitializer testDataInitializer;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private List<UserInfo> savedusers;
    private List<ConcertSeat> savedconcertSeats;
    private UserPoint savedUserPoint;

    private String token;
    private Long userId;
    private Long concertSeatId;
    private Long reservationId;
    private int price;

    @BeforeEach
    void setUp() {
        // 모든 키 삭제
        redisTemplate.getConnectionFactory().getConnection().flushDb();

        testDataInitializer.initializeTestData();

        // initializer 로 적재된 초기 데이터 세팅
        savedusers = testDataInitializer.getSavedUsers();
        savedconcertSeats = testDataInitializer.getSavedConcertSeats();

        userId = savedusers.get(0).getUserId();
        concertSeatId = savedconcertSeats.get(0).getConcertSeatId();
        price = savedconcertSeats.get(0).getPrice();

        // 초기 활성화 토큰 적재
        Queue queue = Queue.create();
        token = queue.getToken();
        queueRepository.addActive(queue);

        // 적재된 좌석 중 하나 예약상태로 저장
        ConcertSeat seat = savedconcertSeats.get(0);
        seat.setReserved();
        concertRepository.saveSeat(seat);

        // 초기 예약 테이블 적재
        Reservation reservation = concertRepository.saveReservation(Reservation.create(concertSeatId, userId, price));
        reservationId = reservation.getReservationId();

        // 초기 150000 포인트 충전
        int oldPoint = 150000;
        savedUserPoint = UserPoint.builder()
                .userId(userId)
                .point(oldPoint)
                .build();
        userPointService.chargePoint(new UserCommand.ChargePoint(savedUserPoint.getUserId(), savedUserPoint.getPoint()));
    }

    @Test
    @DisplayName("🔴 결제_요청_동시성_테스트_결제_요청을_따닥_클릭시_하나를_제외하고_실패해야한다")
    void concurrentRequestPaymentTest_결제_요청_동시성_테스트_결제_요청을_따닥_클릭시_하나를_제외하고_실패해야한다()  {
        // Given
        // 결제 요청 command 객체 생성
        PaymentCreteria.Pay creteria = new PaymentCreteria.Pay(userId, reservationId, price, token);

        // 10개의 스레드를 통해 동시에 요청 시도
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
                    paymentFacade.pay(creteria);
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
        // 최종적으로 한번의 포인트 차감만 성공했는지 확인
        Optional<UserPoint> userPoint = userPointRepository.getUserPoint(userId);
        assertEquals(savedUserPoint.getPoint() - price, userPoint.get().getPoint());

        // 예약 ID로 하나만 적재 확인
        List<Payment> payments = paymentRepository.findByReservationId(reservationId);
        assertThat(payments).hasSize(1);

        // 예외 발생 스레드 개수 체크 (단 하나의 스레드만 성공했는지 검증)
        int numberOfExceptions = exceptions.size();
        assertTrue(numberOfExceptions == numberOfThreads - 1, "예외 발생 스레드 개수 불일치");
    }

}


