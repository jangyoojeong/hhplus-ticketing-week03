package org.hhplus.ticketing.application.payment.facade;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.consert.ConcertService;
import org.hhplus.ticketing.domain.consert.model.ConcertResult;
import org.hhplus.ticketing.domain.payment.PaymentService;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.payment.model.PaymentResult;
import org.hhplus.ticketing.domain.queue.QueueService;
import org.hhplus.ticketing.domain.queue.model.QueueResult;
import org.hhplus.ticketing.domain.user.UserPointService;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 결제 관련 비즈니스 로직을 캡슐화하는 파사드 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final ConcertService concertService;
    private final UserPointService userPointService;
    private final QueueService queueService;

    /**
     * 예약한 좌석의 결제요청을 처리합니다.
     *
     * @param command 결제 요청 command 객체
     * @return 결제 result 객체
     */
    @Transactional
    public PaymentResult.PaymentProcessingResult requestPayment(UUID token, PaymentCommand.PaymentProcessingCommand command) {

        // 1. 예약 정보 조회 (예약 정보 만료 확인)
        // 조회결과 없을 시 "예약 정보를 찾을 수 없거나 이미 만료된 예약입니다." 예외
        ConcertResult.GetReservationInfoResult reservationInfo = concertService.getReservationInfo(command.getReservationId());

        // 2. 포인트 잔액 차감 (포인트 부족할 시 예외 반환)
        // 포인트 부족할 시 "포인트가 부족합니다." 예외
        UserResult.UsePointResult pointResult = userPointService.useUserPoint(new UserCommand.UsePointCommand(command.getUserId(), command.getPrice()));

        // 3. 결제 등록
        PaymentResult.PaymentProcessingResult paymentResult = paymentService.requestPayment(command);

        // 4. 좌석 소유권 배정 (예약됨 > 점유)
        ConcertResult.AssignSeatOwnershipResult seatResult = concertService.assignSeatOwnership(reservationInfo.getReservationId(), reservationInfo.getConcertSeatId());

        // 5. 대기열 토큰 만료
        // 포인트 부족할 시 "포인트가 부족합니다." 예외
        QueueResult.expireTokenResult queueResult = queueService.expireToken(token);

        // 6. 결제 결과 객체에 추가 정보 할당
        return paymentResult.toBuilder()
                .userId(command.getUserId())
                .point(pointResult.getPoint())
                .build();
    }
}
