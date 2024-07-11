package org.hhplus.ticketing.infra.payment.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.payment.PaymentRepository;
import org.hhplus.ticketing.domain.payment.model.PaymentDomain;
import org.hhplus.ticketing.infra.consert.entity.ConcertSeat;
import org.hhplus.ticketing.infra.consert.repository.ConcertJpaRepository;
import org.hhplus.ticketing.infra.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public PaymentDomain save(PaymentDomain domain) {
        return paymentJpaRepository.save(Payment.from(domain)).toDomain();
    }
}
