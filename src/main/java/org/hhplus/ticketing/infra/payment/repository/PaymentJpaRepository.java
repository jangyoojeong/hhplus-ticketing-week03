package org.hhplus.ticketing.infra.payment.repository;

import org.hhplus.ticketing.infra.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentJpaRepository  extends JpaRepository<Payment, Long> {
}
