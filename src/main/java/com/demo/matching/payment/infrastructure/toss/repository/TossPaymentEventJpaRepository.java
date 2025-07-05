package com.demo.matching.payment.infrastructure.toss.repository;

import com.demo.matching.payment.infrastructure.toss.entity.TossPaymentEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TossPaymentEventJpaRepository extends JpaRepository<TossPaymentEventEntity, Long> {
    Optional<TossPaymentEventEntity> findByOrderId(String orderId);
}
