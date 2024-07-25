package org.hhplus.ticketing.domain.payment;

import org.hhplus.ticketing.domain.payment.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    /**
     * 결제 정보를 저장합니다.
     *
     * @param domain 저장할 결제 정보
     * @return 저장된 결제 정보
     */
    Payment save(Payment domain);

    /**
     * 결제 정보를 조회합니다.
     *
     * @param paymentId 조회할 결제 ID
     * @return 조회된 결제 정보, 해당 결제 ID가 없으면 Optional.empty() 반환
     */
    Optional<Payment> findById(Long paymentId);

    /**
     * 예약 ID로 결제 정보를 조회합니다.
     *
     * @param reservationId 조회할 예약 ID
     * @return 조회된 결제 정보, 해당 예약 ID가 없으면 Optional.empty() 반환
     */
    List<Payment> findByReservationId(Long reservationId);
}
