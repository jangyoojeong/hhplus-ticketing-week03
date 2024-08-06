package org.hhplus.ticketing.application.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.concert.ConcertService;
import org.hhplus.ticketing.domain.concert.model.ConcertResult;
import org.hhplus.ticketing.domain.payment.PaymentService;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.payment.model.PaymentResult;
import org.hhplus.ticketing.domain.queue.QueueService;
import org.hhplus.ticketing.domain.user.UserPointService;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결제 관련 비즈니스 로직을 캡슐화하는 파사드 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final QueueService queueService;
    private final PaymentService paymentService;
    private final ConcertService concertService;
    private final UserPointService userPointService;

    /**
     * 예약한 좌석의 결제를 처리합니다.
     *
     * @param command 결제 요청 command 객체
     * @return 결제 result 객체
     */
    @Transactional
    public PaymentResult.Pay pay(String token, PaymentCommand.Pay command) {

        // 1. 좌석 소유권 배정 (예약됨 > 점유)
        ConcertResult.AssignSeat seatResult = concertService.assignSeat(command.getReservationId());

        // 2. 결제 금액 설정 (좌석 정보의 가격 사용)
        command.setPrice(seatResult.getPrice());

        // 3. 포인트 잔액 차감 (포인트 부족할 시 예외 반환 > "포인트가 부족합니다.")
        UserResult.UsePoint pointResult = userPointService.usePoint(new UserCommand.UsePoint(command.getUserId(), command.getPrice()));

        // 5. 대기열 토큰 만료 (토큰 정보 없을 시 예외 반환 > "토큰 정보가 존재하지 않습니다.")
        queueService.expireToken(token);

        // 6. 결제 정보 저장
        return paymentService.createPayment(command).toBuilder()
                .userId(command.getUserId())
                .point(pointResult.getPoint())
                .build();
    }
}
