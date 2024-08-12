package org.hhplus.ticketing.domain.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.payment.event.PaymentEvent;
import org.hhplus.ticketing.domain.payment.event.PaymentEventPublisher;
import org.hhplus.ticketing.domain.payment.model.Payment;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
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
    private final PaymentEventPublisher eventPublisher;

    /**
     * 예약한 좌석의 결제요청을 처리합니다.
     *
     * @param command 결제 요청 command 객체
     * @return 결제 result 객체
     */
    @Transactional
    public Payment pay(PaymentCommand.Pay command) {
        // 1. 결제 생성
        Payment payment = paymentRepository.save(Payment.from(command));
        // 2. 결제 성공 이벤트 발행
        eventPublisher.success(PaymentEvent.Success.builder()
                .token(command.getToken())
                .userId(command.getUserId())
                .reservationId(command.getReservationId())
                .price(command.getPrice())
                .build());
        return payment;
    }
}
