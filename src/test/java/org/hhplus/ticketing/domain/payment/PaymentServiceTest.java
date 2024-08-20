package org.hhplus.ticketing.domain.payment;

import org.hhplus.ticketing.domain.payment.event.PaymentEvent;
import org.hhplus.ticketing.domain.payment.event.PaymentEventPublisher;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.payment.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

// 결제 서비스 단위테스트입니다.
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentEventPublisher eventPublisher;

    private Payment payment;
    private int price;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        price = 1000;

        payment = Payment.builder()
                .paymentId(1L)
                .reservationId(1L)
                .price(price)
                .paymentAt(LocalDateTime.now())
                .status(Payment.Status.COMPLETED)
                .build();
    }

    @Test
    @DisplayName("🟢 [결제요청_테스트]")
    void payTest_결제를_생성하고_관련_이벤트를_성공적으로_발행한다() {

        // Given
        String token = UUID.randomUUID().toString();
        PaymentCommand.Pay command = new PaymentCommand.Pay(1L, 1L, price, token);
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);

        // When
        Payment result = paymentService.pay(command);

        // Then
        assertNotNull(result);
        assertEquals(payment.getPaymentId(), result.getPaymentId());
        verify(paymentRepository).save(any(Payment.class));
        verify(eventPublisher).success(any(PaymentEvent.Success.class));
    }
}