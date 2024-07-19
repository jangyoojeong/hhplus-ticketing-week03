package org.hhplus.ticketing.infra.payment.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.payment.PaymentRepository;
import org.hhplus.ticketing.domain.payment.model.PaymentDomain;
import org.hhplus.ticketing.infra.concert.entity.Reservation;
import org.hhplus.ticketing.infra.payment.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public PaymentDomain save(PaymentDomain domain) {
        return paymentJpaRepository.save(Payment.from(domain)).toDomain();
    }

    @Override
    public Optional<PaymentDomain> findById(Long paymentId) {
        return paymentJpaRepository.findById(paymentId).map(Payment::toDomain);
    }
}
