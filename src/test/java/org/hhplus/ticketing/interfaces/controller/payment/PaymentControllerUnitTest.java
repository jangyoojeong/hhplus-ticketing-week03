package org.hhplus.ticketing.interfaces.controller.payment;

import org.hhplus.ticketing.application.payment.PaymentFacade;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.payment.model.PaymentResult;
import org.hhplus.ticketing.interfaces.controller.payment.dto.request.PaymentRequest;
import org.hhplus.ticketing.interfaces.controller.payment.dto.response.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

public class PaymentControllerUnitTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentFacade paymentFacade;

    private Long userId;
    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        token = UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("🟢 결제_컨트롤러_테스트_예상_리턴_확인")
    void requestPaymentTest_결제_컨트롤러_테스트_예상_리턴_확인 () throws Exception {
        // Given
        Long reservationId = 1L;
        int point = 2000;

        PaymentRequest.PaymentProcessingRequest request = new PaymentRequest.PaymentProcessingRequest(userId, reservationId);
        PaymentResult.RequestPaymentResult result = new PaymentResult.RequestPaymentResult(1L, userId, point);
        PaymentResponse.PaymentProcessingResponse response = PaymentResponse.PaymentProcessingResponse.from(result);

        given(paymentFacade.requestPayment(eq(token), any(PaymentCommand.RequestPaymentCommand.class))).willReturn(result);

        // When
        ResponseEntity<PaymentResponse.PaymentProcessingResponse> responseEntity = paymentController.requestPayment("Bearer " + token, request);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }
}