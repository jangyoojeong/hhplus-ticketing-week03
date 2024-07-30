package org.hhplus.ticketing.integration;

import org.hhplus.ticketing.application.payment.PaymentFacade;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.concert.ConcertRepository;
import org.hhplus.ticketing.domain.concert.model.ConcertSeat;
import org.hhplus.ticketing.domain.concert.model.Reservation;
import org.hhplus.ticketing.domain.payment.PaymentRepository;
import org.hhplus.ticketing.domain.payment.model.Payment;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.payment.model.PaymentResult;
import org.hhplus.ticketing.domain.queue.QueueRepository;
import org.hhplus.ticketing.domain.queue.model.Queue;
import org.hhplus.ticketing.domain.user.UserPointService;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserInfo;
import org.hhplus.ticketing.domain.user.model.UserPoint;
import org.hhplus.ticketing.utils.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
// @DirtiesContext 컨텍스트의 상태를 초기화
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaymentIntegrationTest {

    @Autowired
    private PaymentFacade paymentFacade;
    @Autowired
    private UserPointService userPointService;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    TestDataInitializer testDataInitializer;

    private List<UserInfo> savedusers;
    private List<ConcertSeat> savedconcertSeats;

    private UserPoint savedUserPoint;

    private UUID token;
    private Long userId;
    private Long concertSeatId;
    private Long reservationId;
    private int price;

    @BeforeEach
    void setUp() {
        testDataInitializer.initializeTestData();

        // initializer 로 적재된 초기 데이터 세팅
        savedusers = testDataInitializer.getSavedUsers();
        savedconcertSeats = testDataInitializer.getSavedConcertSeats();

        userId = savedusers.get(0).getUserId();
        concertSeatId = savedconcertSeats.get(0).getConcertSeatId();
        price = savedconcertSeats.get(0).getPrice();

        // 초기 활성화 토큰 적재
        Queue queue = Queue.builder()
                .userId(userId)
                .token(UUID.randomUUID())
                .status(Queue.Status.ACTIVE)
                .enteredAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .build();
        Queue savedQueue = queueRepository.save(queue);
        token = savedQueue.getToken();

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
        userPointService.chargePoint(new UserCommand.ChargePointCommand(savedUserPoint.getUserId(), savedUserPoint.getPoint()));
    }

    @Test
    @DisplayName("🟢 결제_요청_통합_테스트_결제가_성공하고_기존_50000포인트에서_30000포인트가_차감된_20000포인트가_리턴된다")
    void requestPaymentTest_결제_요청_통합_테스트_결제가_성공하고_기존_50000포인트에서_30000포인트가_차감된_20000포인트가_리턴된다() {

        // Given
        PaymentCommand.RequestPaymentCommand command = new PaymentCommand.RequestPaymentCommand(userId, reservationId, price);

        // When
        PaymentResult.RequestPaymentResult actualResult = paymentFacade.requestPayment(token, command);

        // Then
        assertNotNull(actualResult);
        assertEquals(savedUserPoint.getPoint() - price, actualResult.getPoint());
    }

    @Test
    @DisplayName("🟢 결제_요청_통합_테스트_결제가_성공하고_결제정보가_적재된다")
    void requestPaymentTest_결제_요청_통합_테스트_결제가_성공하고_결제정보가_적재된다() {

        // Given
        PaymentCommand.RequestPaymentCommand command = new PaymentCommand.RequestPaymentCommand(userId, reservationId, price);

        // When
        PaymentResult.RequestPaymentResult actualResult = paymentFacade.requestPayment(token, command);

        // Then
        Optional<Payment> paymentDomain = paymentRepository.findById(actualResult.getPaymentId());
        assertNotNull(actualResult);
        assertTrue(paymentDomain.isPresent(), "결제 정보가 적재되지 않았습니다.");
    }

    @Test
    @DisplayName("🟢 결제_요청_통합_테스트_결제가_성공하고_좌석_소유권이_배정된다")
    void requestPaymentTest_결제_요청_통합_테스트_결제가_성공하고_좌석_소유권이_배정된다() {

        // Given
        PaymentCommand.RequestPaymentCommand command = new PaymentCommand.RequestPaymentCommand(userId, reservationId, price);

        // When
        PaymentResult.RequestPaymentResult actualResult = paymentFacade.requestPayment(token, command);

        // Then
        Optional<Reservation> reservation = concertRepository.findReservationById(reservationId);
        Optional<ConcertSeat> seat = concertRepository.findSeatById(reservation.get().getConcertSeatId());
        assertNotNull(actualResult);
        assertEquals(Reservation.Status.OCCUPIED, reservation.get().getStatus());
        assertEquals(ConcertSeat.Status.OCCUPIED, seat.get().getStatus());
    }

    @Test
    @DisplayName("🟢 결제_요청_통합_테스트_결제가_성공하고_대기열_토큰이_만료된다")
    void requestPaymentTest_결제_요청_통합_테스트_결제가_성공하고_대기열_토큰이_만료된다() {

        // Given
        PaymentCommand.RequestPaymentCommand command = new PaymentCommand.RequestPaymentCommand(userId, reservationId, price);

        // When
        PaymentResult.RequestPaymentResult actualResult = paymentFacade.requestPayment(token, command);

        // Then
        Optional<Queue> queue = queueRepository.findByToken(token);

        assertNotNull(actualResult);
        assertEquals(Queue.Status.EXPIRED, queue.get().getStatus());
    }

    @Test
    @DisplayName("🔴 결제_요청_통합_테스트_예약정보_없으면_RESERVATION_NOT_FOUND_예외반환")
    void requestPaymentTest_결제_요청_통합_테스트_예약정보_없으면_RESERVATION_NOT_FOUND_예외반환() {

        // Given
        Long nonExistentReservationId = 99L;    // 존재하지 않는 예약코드

        PaymentCommand.RequestPaymentCommand command = new PaymentCommand.RequestPaymentCommand(userId, nonExistentReservationId, price);

        // When & Then
        assertThatThrownBy(() -> paymentFacade.requestPayment(token, command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESERVATION_NOT_FOUND);
    }

    @Test
    @DisplayName("🔴 결제_요청_통합_테스트_결제금액에_비해_포인트가_부족할경우_INSUFFICIENT_POINTS_예외반환")
    void requestPaymentTest_결제_요청_통합_테스트_결제금액에_비해_포인트가_부족할경우_INSUFFICIENT_POINTS_예외반환() {

        // Given
        int amount = 100000;
        userPointService.usePoint(new UserCommand.UsePointCommand(savedUserPoint.getUserId(), amount));

        PaymentCommand.RequestPaymentCommand command = new PaymentCommand.RequestPaymentCommand(userId, reservationId, price);

        // When & Then
        assertThatThrownBy(() -> paymentFacade.requestPayment(token, command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INSUFFICIENT_POINTS);
    }

    @Test
    @DisplayName("🔴 결제_요청_통합_테스트_대기열_토큰_정보가_없을_시_TOKEN_NOT_FOUND_예외반환")
    public void requestPaymentTest_결제_요청_통합_테스트_대기열_토근_정보가_없을_시_TOKEN_NOT_FOUND_예외반환() {

        // Given
        UUID nonExistentToken = UUID.randomUUID();

        PaymentCommand.RequestPaymentCommand command = new PaymentCommand.RequestPaymentCommand(userId, reservationId, price);

        // When & Then
        assertThatThrownBy(() -> paymentFacade.requestPayment(nonExistentToken, command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOKEN_NOT_FOUND);
    }

}
