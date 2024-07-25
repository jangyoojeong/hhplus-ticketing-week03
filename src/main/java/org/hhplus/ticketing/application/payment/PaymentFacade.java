package org.hhplus.ticketing.application.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ticketing.domain.common.exception.CustomException;
import org.hhplus.ticketing.domain.common.exception.ErrorCode;
import org.hhplus.ticketing.domain.payment.PaymentService;
import org.hhplus.ticketing.domain.payment.model.PaymentCommand;
import org.hhplus.ticketing.domain.payment.model.PaymentResult;
import org.hhplus.ticketing.domain.queue.QueueService;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 결제 관련 비즈니스 로직을 캡슐화하는 파사드 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final QueueService queueService;
    private final UserReservationAggregate userReservationAggregate;

    /**
     * 예약한 좌석의 결제요청을 처리합니다.
     *
     * @param command 결제 요청 command 객체
     * @return 결제 result 객체
     */
    public PaymentResult.PaymentProcessingResult requestPayment(UUID token, PaymentCommand.PaymentProcessingCommand command) {

        int point = 0;

        try {
            // 1. 좌석 소유권 배정 및 포인트 차감
            point = userReservationAggregate.processSeatAndPoints(command);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(ErrorCode.DUPLICATE_REQUEST, e);
        }

        log.info("dfdfd {}", command.getPrice());

        // 2. 결제 등록
        PaymentResult.PaymentProcessingResult paymentResult = paymentService.requestPayment(command);

        // 3. 대기열 토큰 만료 (토큰 정보 없을 시 예외 반환 > "토큰 정보가 존재하지 않습니다.")
        queueService.expireToken(token);

        // 4. 결제 결과 객체에 추가 정보 할당
        return paymentResult.toBuilder()
                .userId(command.getUserId())
                .point(point)
                .build();
    }
}
