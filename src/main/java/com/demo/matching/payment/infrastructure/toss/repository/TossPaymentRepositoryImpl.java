package com.demo.matching.payment.infrastructure.toss.repository;

import com.demo.matching.payment.application.toss.port.in.TossPaymentRepository;
import com.demo.matching.payment.domain.toss.TossPayment;
import com.demo.matching.payment.infrastructure.toss.entity.TossPaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TossPaymentRepositoryImpl implements TossPaymentRepository {
    private final TossPaymentJpaRepository tossPaymentJpaRepository;
    public TossPayment save(TossPayment tossPayment) {
        return tossPaymentJpaRepository.save(TossPaymentEntity.from(tossPayment)).to();
    }
}
