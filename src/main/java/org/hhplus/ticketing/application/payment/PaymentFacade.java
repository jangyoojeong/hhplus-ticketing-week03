package org.hhplus.ticketing.application.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.concert.ConcertService;
import org.hhplus.ticketing.domain.concert.model.Reservation;
import org.hhplus.ticketing.domain.payment.PaymentService;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.springframework.stereotype.Component;

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
     * @param criteria 결제 요청 criteria 객체
     * @return 결제 result 객체
     */
    public PaymentResult.Pay pay(PaymentCriteria.Pay criteria) {
        Reservation reservation = concertService.getReservation(criteria.getReservationId());
        PaymentCommand.Pay paymentCommand = PaymentCommand.Pay.builder()
                .userId(criteria.getUserId())
                .reservationId(criteria.getReservationId())
                .price(reservation.getPrice())
                .token(criteria.getToken())
                .build();
        return new PaymentResult.Pay(paymentService.pay(paymentCommand).getPaymentId());
    }
}
