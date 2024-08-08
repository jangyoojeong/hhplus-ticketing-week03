package org.hhplus.ticketing.application.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.concert.ConcertService;
import org.hhplus.ticketing.domain.concert.model.Reservation;
import org.hhplus.ticketing.domain.payment.PaymentService;
import org.hhplus.ticketing.domain.payment.model.Payment;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.queue.QueueService;
import org.hhplus.ticketing.domain.user.UserPointService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결제 관련 비즈니스 로직을 캡슐화하는 파사드 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final ConcertService concertService;

    /**
     * 예약한 좌석의 결제를 처리합니다.
     *
     * @param creteria 결제 요청 creteria 객체
     * @return 결제 result 객체
     */
    public PaymentResult.Pay pay(PaymentCreteria.Pay creteria) {
        log.info("결제파사드 진입");
        Reservation reservation = concertService.getReservation(creteria.getReservationId());
        PaymentCommand.Pay paymentCommand = PaymentCommand.Pay.builder()
                .userId(creteria.getUserId())
                .reservationId(creteria.getReservationId())
                .price(reservation.getPrice())
                .token(creteria.getToken())
                .build();
        return new PaymentResult.Pay(paymentService.pay(paymentCommand).getPaymentId());
    }
}
