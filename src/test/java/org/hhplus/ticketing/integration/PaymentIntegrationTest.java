package org.hhplus.ticketing.integration;

import org.hhplus.ticketing.application.payment.facade.PaymentFacade;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.concert.ConcertRepository;
import org.hhplus.ticketing.domain.concert.model.ConcertSeatDomain;
import org.hhplus.ticketing.domain.concert.model.ReservationDomain;
import org.hhplus.ticketing.domain.concert.model.enums.ReservationStatus;
import org.hhplus.ticketing.domain.concert.model.enums.SeatStatus;
import org.hhplus.ticketing.domain.payment.PaymentRepository;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.payment.model.PaymentDomain;
import org.hhplus.ticketing.domain.payment.model.PaymentResult;
import org.hhplus.ticketing.domain.queue.QueueRepository;
import org.hhplus.ticketing.domain.queue.model.QueueDomain;
import org.hhplus.ticketing.domain.queue.model.enums.TokenStatus;
import org.hhplus.ticketing.domain.user.UserPointRepository;
import org.hhplus.ticketing.domain.user.model.UserInfoDomain;
import org.hhplus.ticketing.domain.user.model.UserPointDomain;
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
    private ConcertRepository concertRepository;
    @Autowired
    private UserPointRepository userPointRepository;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    TestDataInitializer testDataInitializer;

    private List<UserInfoDomain> savedusers;
    private List<ConcertSeatDomain> savedconcertSeats;

    private UserPointDomain userPointDomain;

    private UUID token;
    private Long userId;
    private Long concertSeatId;
    private Long reservationId;
    private int price;

    @BeforeEach
    void setUp() {
        testDataInitializer.initializeTestData();

        // initializer 로 적재된 초기 데이터 세팅
        savedusers = testDataInitializer.getSavedusers();
        savedconcertSeats = testDataInitializer.getSavedconcertSeats();

        token = UUID.randomUUID();
        userId = savedusers.get(0).getUserId();
        concertSeatId = savedconcertSeats.get(0).getConcertSeatId();
        price = 30000;

        // 초기 활성화 토큰 적재
        QueueDomain queueDomain = QueueDomain.builder()
                .userId(userId)
                .token(token)
                .status(TokenStatus.ACTIVE)
                .enteredAt(LocalDateTime.now())
                .build();

        queueRepository.save(queueDomain);

        // 적재된 좌석 중 하나 예약상태로 저장
        ConcertSeatDomain seatDomain = savedconcertSeats.get(0);
        seatDomain.updateSeatReserved();
        concertRepository.saveSeat(seatDomain);

        // 초기 예약 테이블 적재
        ReservationDomain reservationDomain = concertRepository.saveReservation(ReservationDomain.createReservation(concertSeatId, userId));
        reservationId = reservationDomain.getReservationId();

        // 초기 50000 포인트 적재
        userPointDomain = UserPointDomain.builder()
                .userId(userId)
                .point(50000)
                .build();

        userPointRepository.save(userPointDomain);
    }

    @Test
    @DisplayName("[성공테스트] 결제_요청_통합_테스트_결제가_성공하고_기존_50000포인트에서_30000포인트가_차감된_20000포인트가_리턴된다")
    void requestPaymentTest_결제_요청_통합_테스트_결제가_성공하고_기존_50000포인트에서_30000포인트가_차감된_20000포인트가_리턴된다() {

        // Given
        PaymentCommand.PaymentProcessingCommand command = new PaymentCommand.PaymentProcessingCommand(userId, reservationId, price);

        // When
        PaymentResult.PaymentProcessingResult actualResult = paymentFacade.requestPayment(token, command);

        // Then
        assertNotNull(actualResult);
        assertEquals(userPointDomain.getPoint() - price, actualResult.getPoint());
    }

    @Test
    @DisplayName("[성공테스트] 결제_요청_통합_테스트_결제가_성공하고_결제정보가_적재된다")
    void requestPaymentTest_결제_요청_통합_테스트_결제가_성공하고_결제정보가_적재된다() {

        // Given
        PaymentCommand.PaymentProcessingCommand command = new PaymentCommand.PaymentProcessingCommand(userId, reservationId, price);

        // When
        PaymentResult.PaymentProcessingResult actualResult = paymentFacade.requestPayment(token, command);

        // Then
        Optional<PaymentDomain> paymentDomain = paymentRepository.findById(actualResult.getPaymentId());
        assertNotNull(actualResult);
        assertTrue(paymentDomain.isPresent(), "결제 정보가 적재되지 않았습니다.");
    }
    
    @Test
    @DisplayName("[성공테스트] 결제_요청_통합_테스트_결제가_성공하고_좌석_소유권이_배정된다")
    void requestPaymentTest_결제_요청_통합_테스트_결제가_성공하고_좌석_소유권이_배정된다() {

        // Given
        PaymentCommand.PaymentProcessingCommand command = new PaymentCommand.PaymentProcessingCommand(userId, reservationId, price);

        // When
        PaymentResult.PaymentProcessingResult actualResult = paymentFacade.requestPayment(token, command);

        // Then
        Optional<ReservationDomain> reservationDomain = concertRepository.findReservationById(reservationId);
        Optional<ConcertSeatDomain> seatDomain = concertRepository.findSeatById(reservationDomain.get().getConcertSeatId());
        assertNotNull(actualResult);
        assertEquals(ReservationStatus.OCCUPIED, reservationDomain.get().getStatus());
        assertEquals(SeatStatus.OCCUPIED, seatDomain.get().getStatus());
    }
    
    @Test
    @DisplayName("[성공테스트] 결제_요청_통합_테스트_결제가_성공하고_대기열_토큰이_만료된다")
    void requestPaymentTest_결제_요청_통합_테스트_결제가_성공하고_대기열_토큰이_만료된다() {

        // Given
        PaymentCommand.PaymentProcessingCommand command = new PaymentCommand.PaymentProcessingCommand(userId, reservationId, price);

        // When
        PaymentResult.PaymentProcessingResult actualResult = paymentFacade.requestPayment(token, command);

        // Then
        Optional<QueueDomain> queueDomain = queueRepository.findByToken(token);

        assertNotNull(actualResult);
        assertEquals(TokenStatus.EXPIRED, queueDomain.get().getStatus());
    }

    @Test
    @DisplayName("[실패테스트] 결제_요청_통합_테스트_예약정보_없으면_RESERVATION_NOT_FOUND_예외반환")
    void requestPaymentTest_결제_요청_통합_테스트_예약정보_없으면_RESERVATION_NOT_FOUND_예외반환() {

        // Given
        Long nonExistentReservationId = 99L;    // 존재하지 않는 예약코드

        PaymentCommand.PaymentProcessingCommand command = new PaymentCommand.PaymentProcessingCommand(userId, nonExistentReservationId, price);

        // When & Then
        assertThatThrownBy(() -> paymentFacade.requestPayment(token, command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESERVATION_NOT_FOUND);
    }

    @Test
    @DisplayName("[실패테스트] 결제_요청_통합_테스트_결제금액에_비해_포인트가_부족할경우_INSUFFICIENT_POINTS_예외반환")
    void requestPaymentTest_결제_요청_통합_테스트_결제금액에_비해_포인트가_부족할경우_INSUFFICIENT_POINTS_예외반환() {

        // Given
        int overPrice = 60000;

        PaymentCommand.PaymentProcessingCommand command = new PaymentCommand.PaymentProcessingCommand(userId, reservationId, overPrice);

        // When & Then
        assertThatThrownBy(() -> paymentFacade.requestPayment(token, command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INSUFFICIENT_POINTS);
    }

    @Test
    @DisplayName("[실패테스트] 결제_요청_통합_테스트_대기열_토근_정보가_없을_시_TOKEN_NOT_FOUND_예외반환")
    public void requestPaymentTest_결제_요청_통합_테스트_대기열_토근_정보가_없을_시_TOKEN_NOT_FOUND_예외반환() {

        // Given
        UUID nonExistentToken = UUID.randomUUID();

        PaymentCommand.PaymentProcessingCommand command = new PaymentCommand.PaymentProcessingCommand(userId, reservationId, price);

        // When & Then
        assertThatThrownBy(() -> paymentFacade.requestPayment(nonExistentToken, command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TOKEN_NOT_FOUND);
    }

}
