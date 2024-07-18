package org.hhplus.ticketing.domain.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.payment.model.PaymentDomain;
import org.hhplus.ticketing.domain.payment.model.PaymentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결제 관련 비즈니스 로직을 담당하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * 예약한 좌석의 결제요청을 처리합니다.
     *
     * @param command 결제 요청 command 객체
     * @return 결제 result 객체
     */
    @Transactional
    public PaymentResult.PaymentProcessingResult requestPayment(PaymentCommand.PaymentProcessingCommand command) {
        return PaymentResult.PaymentProcessingResult.from(paymentRepository.save(PaymentDomain.from(command)));
    }
}
