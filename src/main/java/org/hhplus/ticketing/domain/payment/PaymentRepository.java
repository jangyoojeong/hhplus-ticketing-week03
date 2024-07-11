package org.hhplus.ticketing.domain.payment;

import org.hhplus.ticketing.domain.consert.model.ConcertSeatDomain;
import org.hhplus.ticketing.domain.payment.model.PaymentDomain;

public interface PaymentRepository {

    /**
     * 결제 정보를 저장합니다.
     *
     * @param domain 저장할 예약 정보
     * @return domain 저장된 예약 정보
     */
    PaymentDomain save(PaymentDomain domain);

}
