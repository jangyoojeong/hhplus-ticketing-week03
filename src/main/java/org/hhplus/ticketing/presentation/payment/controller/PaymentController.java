package org.hhplus.ticketing.presentation.payment.controller;

import org.hhplus.ticketing.presentation.payment.dto.request.PaymentRequest;
import org.hhplus.ticketing.presentation.payment.dto.response.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    /**
     * 예약한 좌석의 결제요청을 처리합니다.
     *
     * @param request 결제요청 객체
     * @return 결제요청 응답 객체
     */
    @PostMapping("/")
    public ResponseEntity<PaymentResponse> requestPayment (@RequestHeader("Authorization") String authorizationHeader, @RequestBody PaymentRequest request) {

        String token = authorizationHeader.replace("Bearer ", "");

        PaymentResponse response = new PaymentResponse(460L, request.getUuid(), 2000);
        return ResponseEntity.ok(response);
    }

}
