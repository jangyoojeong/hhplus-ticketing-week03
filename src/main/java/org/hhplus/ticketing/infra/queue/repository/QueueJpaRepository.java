package org.hhplus.ticketing.infra.queue.repository;

import org.hhplus.ticketing.infra.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueJpaRepository  extends JpaRepository<Payment, Long> {
}
