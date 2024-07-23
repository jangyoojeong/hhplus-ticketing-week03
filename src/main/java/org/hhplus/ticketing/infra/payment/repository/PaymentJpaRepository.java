package org.hhplus.ticketing.infra.payment.repository;

import org.hhplus.ticketing.infra.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository  extends JpaRepository<PaymentEntity, Long> {

}
