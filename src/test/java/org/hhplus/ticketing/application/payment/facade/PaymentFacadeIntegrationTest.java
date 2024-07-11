package org.hhplus.ticketing.application.payment.facade;

import org.hhplus.ticketing.domain.consert.ConcertService;
import org.hhplus.ticketing.domain.consert.model.ConcertResult;
import org.hhplus.ticketing.domain.payment.PaymentService;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.payment.model.PaymentResult;
import org.hhplus.ticketing.domain.queue.QueueService;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.user.UserPointService;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

// 결제 파사드&서비스 통합테스트입니다.
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 모든 테스트가 독립적으로 실행되도록 보장
class PaymentFacadeIntegrationTest {

    @Autowired
    private PaymentFacade paymentFacade;
    @MockBean
    private PaymentService paymentService;
    @MockBean
    private ConcertService concertService;
    @MockBean
    private UserPointService userPointService;
    @MockBean
    private QueueService queueService;

    private UUID token;
    private Long userId;
    private PaymentCommand.PaymentProcessingCommand paymentCommand;
    private ConcertResult.GetReservationInfoResult reservationInfoResult;
    private UserResult.UsePointResult usePointResult;
    private PaymentResult.PaymentProcessingResult paymentProcessingResult;
    private ConcertResult.AssignSeatOwnershipResult assignSeatOwnershipResult;
    private QueueResult.expireTokenResult expireTokenResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        token = UUID.randomUUID();
        userId = 1L;

        paymentCommand = PaymentCommand.PaymentProcessingCommand.builder()
                .userId(userId)
                .reservationId(1L)
                .price(10000)
                .build();

        reservationInfoResult = ConcertResult.GetReservationInfoResult.builder()
                .reservationId(userId)
                .concertSeatId(1L)
                .build();

        usePointResult = UserResult.UsePointResult.builder()
                .userId(userId)
                .point(5000)
                .build();

        paymentProcessingResult = PaymentResult.PaymentProcessingResult.builder()
                .paymentId(1L)
                .userId(1L)
                .point(5000)
                .build();

        assignSeatOwnershipResult = ConcertResult.AssignSeatOwnershipResult.builder()
                .concertSeatId(1L)
                .build();

        expireTokenResult = QueueResult.expireTokenResult.builder()
                .userId(1L)
                .build();

        given(concertService.getReservationInfo(anyLong())).willReturn(reservationInfoResult);
        given(userPointService.useUserPoint(any(UserCommand.UsePointCommand.class))).willReturn(usePointResult);
        given(paymentService.requestPayment(any(PaymentCommand.PaymentProcessingCommand.class))).willReturn(paymentProcessingResult);
        given(concertService.assignSeatOwnership(anyLong(), anyLong())).willReturn(assignSeatOwnershipResult);
        given(queueService.expireToken(any(UUID.class))).willReturn(expireTokenResult);
    }

    @Test
    @DisplayName("[성공테스트] 결제_요청_통합_테스트")
    void requestPaymentTest_결제_요청_통합_테스트() {
        // When
        PaymentResult.PaymentProcessingResult result = paymentFacade.requestPayment(token, paymentCommand);

        // Then
        assertNotNull(result);
        assertEquals(paymentCommand.getUserId(), result.getUserId());
        assertEquals(usePointResult.getPoint(), result.getPoint());

        verify(concertService, times(1)).getReservationInfo(paymentCommand.getReservationId());
        verify(userPointService, times(1)).useUserPoint(any(UserCommand.UsePointCommand.class));
        verify(paymentService, times(1)).requestPayment(paymentCommand);
        verify(concertService, times(1)).assignSeatOwnership(reservationInfoResult.getReservationId(), reservationInfoResult.getConcertSeatId());
        verify(queueService, times(1)).expireToken(token);
    }

    @Test
    @DisplayName("[실패테스트] 결제_요청_통합_테스트_포인트부족_예외발생")
    void requestPaymentTest_결제_요청_통합_테스트_포인트부족_예외발생() {
        // Given
        given(userPointService.useUserPoint(any(UserCommand.UsePointCommand.class)))
                .willThrow(new IllegalArgumentException("포인트가 부족합니다."));

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> paymentFacade.requestPayment(token, paymentCommand));
        assertEquals("포인트가 부족합니다.", exception.getMessage());

        verify(concertService, times(1)).getReservationInfo(paymentCommand.getReservationId());
        verify(userPointService, times(1)).useUserPoint(any(UserCommand.UsePointCommand.class));
        verify(paymentService, times(0)).requestPayment(any(PaymentCommand.PaymentProcessingCommand.class));
        verify(concertService, times(0)).assignSeatOwnership(anyLong(), anyLong());
        verify(queueService, times(0)).expireToken(any(UUID.class));
    }

    @Test
    @DisplayName("[실패테스트] 결제_요청_통합_테스트_예약정보_없음_예외발생")
    void requestPaymentTest_결제_요청_통합_테스트_예약정보_없음_예외발생() {
        // Given
        given(concertService.getReservationInfo(anyLong()))
                .willThrow(new IllegalArgumentException("예약 정보를 찾을 수 없거나 이미 만료된 예약입니다."));

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> paymentFacade.requestPayment(token, paymentCommand));
        assertEquals("예약 정보를 찾을 수 없거나 이미 만료된 예약입니다.", exception.getMessage());

        verify(concertService, times(1)).getReservationInfo(paymentCommand.getReservationId());
        verify(userPointService, times(0)).useUserPoint(any(UserCommand.UsePointCommand.class));
        verify(paymentService, times(0)).requestPayment(any(PaymentCommand.PaymentProcessingCommand.class));
        verify(concertService, times(0)).assignSeatOwnership(anyLong(), anyLong());
        verify(queueService, times(0)).expireToken(any(UUID.class));
    }
}