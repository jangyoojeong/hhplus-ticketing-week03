package org.hhplus.ticketing.domain.payment;

import org.hhplus.ticketing.domain.payment.model.PaymentDomain;

import java.util.Optional;

public interface PaymentRepository {

    /**
     * 결제 정보를 저장합니다.
     *
     * @param domain 저장할 예약 정보
     * @return domain 저장된 예약 정보
     */
    PaymentDomain save(PaymentDomain domain);
    
    /**
     * 결제 정보를 조회합니다.
     *
     * @param paymentId 조회할 결제ID
     * @return domain 조회된 결제 정보
     */
    Optional<PaymentDomain> findById(Long paymentId);
}
