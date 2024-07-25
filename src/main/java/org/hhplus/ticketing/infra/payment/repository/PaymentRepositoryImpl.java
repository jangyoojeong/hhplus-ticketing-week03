package org.hhplus.ticketing.infra.payment.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.payment.PaymentRepository;
import org.hhplus.ticketing.domain.payment.model.Payment;
import org.hhplus.ticketing.infra.payment.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment domain) {
        return paymentJpaRepository.save(PaymentEntity.from(domain)).toDomain();
    }

    @Override
    public Optional<Payment> findById(Long paymentId) {
        return paymentJpaRepository.findById(paymentId).map(PaymentEntity::toDomain);
    }

    @Override
    public List<Payment> findByReservationId(Long reservationId) {
        return PaymentEntity.toDomainList(paymentJpaRepository.findByReservationId(reservationId));
    }
}
