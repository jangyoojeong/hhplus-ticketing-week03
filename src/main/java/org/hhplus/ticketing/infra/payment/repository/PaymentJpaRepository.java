package org.hhplus.ticketing.infra.payment.repository;

import org.hhplus.ticketing.infra.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository  extends JpaRepository<PaymentEntity, Long> {

    /**
     * ReservationId를 기반으로 결제 정보를 조회합니다.
     *
     * @param reservationId 조회할 예약 ID
     * @return ReservationId에 해당하는 결제 {@link Optional} 객체. 만일 해당하는 결제정보가 없으면 비어있는 Optional을 반환합니다.
     */
    List<PaymentEntity> findByReservationId(Long reservationId);
}
