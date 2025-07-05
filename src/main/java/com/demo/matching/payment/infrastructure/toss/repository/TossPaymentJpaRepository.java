package com.demo.matching.payment.infrastructure.toss.repository;

import com.demo.matching.payment.infrastructure.toss.entity.TossPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TossPaymentJpaRepository extends JpaRepository<TossPaymentEntity, Long> {

    Optional<TossPaymentEntity> findByOrderId(String orderId);
}
