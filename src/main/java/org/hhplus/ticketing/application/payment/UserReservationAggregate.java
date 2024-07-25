package org.hhplus.ticketing.application.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.concert.ConcertService;
import org.hhplus.ticketing.domain.concert.model.ConcertResult;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.user.UserPointService;
import org.hhplus.ticketing.domain.user.model.UserCommand;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Component
@RequiredArgsConstructor
public class UserReservationAggregate {

    private final ConcertService concertService;
    private final UserPointService userPointService;

    @Transactional(rollbackFor = {Exception.class})
    public int processSeatAndPoints(PaymentCommand.PaymentProcessingCommand command) {

        // 1. 좌석 소유권 배정 (예약됨 > 점유)
        ConcertResult.AssignSeatResult seatResult = concertService.assignSeat(command.getReservationId());

        // 2. 결제 금액 설정 (좌석 정보의 가격 사용)
        command.setPrice(seatResult.getPrice());

        // 3. 포인트 잔액 차감 (포인트 부족할 시 예외 반환 > "포인트가 부족합니다.")
        UserResult.UsePointResult pointResult = userPointService.usePoint(new UserCommand.UsePointCommand(command.getUserId(), command.getPrice()));

        return pointResult.getPoint();
    }
}
