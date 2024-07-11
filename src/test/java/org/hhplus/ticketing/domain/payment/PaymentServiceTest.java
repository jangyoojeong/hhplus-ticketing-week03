package org.hhplus.ticketing.domain.payment;

import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.payment.model.PaymentDomain;
import org.hhplus.ticketing.domain.payment.model.PaymentResult;
import org.hhplus.ticketing.domain.payment.model.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import static org.mockito.Mockito.times;
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

    private PaymentDomain paymentDomain;
    private PaymentResult.PaymentProcessingResult paymentProcessingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        paymentDomain = PaymentDomain.builder()
                .paymentId(1L)
                .reservationId(1L)
                .price(10000)
                .paymentAt(LocalDateTime.now())
                .status(PaymentStatus.COMPLETED)
                .build();

        paymentProcessingResult = PaymentResult.PaymentProcessingResult.from(paymentDomain);
    }

    @Test
    @DisplayName("[성공테스트] 좌석_결제_요청_정상적으로_실행된다")
    void requestPayment_좌석_결제_요청_정상적으로_실행된다() {

        // Given
        PaymentCommand.PaymentProcessingCommand command = new PaymentCommand.PaymentProcessingCommand(1L, 1L, 10000);
        given(paymentRepository.save(any(PaymentDomain.class))).willReturn(paymentDomain);

        // When
        PaymentResult.PaymentProcessingResult result = paymentService.requestPayment(command);

        // Then
        assertNotNull(result);
        assertEquals(paymentProcessingResult, result);
        verify(paymentRepository, times(1)).save(any(PaymentDomain.class));
    }
}