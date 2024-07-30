package org.hhplus.ticketing.interfaces.controller.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.application.payment.PaymentFacade;
import org.hhplus.ticketing.interfaces.controller.payment.dto.request.PaymentRequest;
import org.hhplus.ticketing.interfaces.controller.payment.dto.response.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 결제 관련 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name = "Payment API", description = "결제 관련 API")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 예약한 좌석의 결제요청을 처리합니다.
     *
     * @param authorizationHeader 인증 토큰을 포함한 헤더
     * @param request 결제요청 객체
     * @return 결제요청 응답 객체
     */
    @PostMapping("/")
    @Operation(summary = "결제 API", description = "예약한 좌석의 결제를 처리합니다.")
    public ResponseEntity<PaymentResponse.PaymentProcessingResponse> requestPayment (@RequestHeader(value = AUTHORIZATION_HEADER, required = true) String authorizationHeader, @Valid @RequestBody PaymentRequest.PaymentProcessingRequest request) {
        String token = authorizationHeader.replace(BEARER_PREFIX, "");
        return ResponseEntity.status(HttpStatus.OK).body(PaymentResponse.PaymentProcessingResponse.from(paymentFacade.requestPayment(UUID.fromString(token), request.toCommand())));
    }
}
