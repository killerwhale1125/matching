package com.demo.matching.payment.infrastructure.toss.repository;

import com.demo.matching.payment.domain.toss.enums.TossPaymentStatus;
import com.demo.matching.payment.infrastructure.toss.entity.TossPaymentEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface TossPaymentEventJpaRepository extends JpaRepository<TossPaymentEventEntity, Long> {
    Optional<TossPaymentEventEntity> findByOrderId(String orderId);
    
    /* 3분 전 & 결제 가능 상태의 결제 이력 조회 (UNKNOWN -> 날짜 기록 없음 )*/
    @Query("SELECT pe " +
            "FROM TossPaymentEventEntity pe " +
            "WHERE ((pe.tossPaymentStatus = :inProgress AND pe.executedAt < :before) " +
            "OR pe.tossPaymentStatus = :unknown)")
    List<TossPaymentEventEntity> findByInProgressWithTimeConstraintOrUnknown(
            @Param("before") LocalDateTime before,
            @Param("inProgress") TossPaymentStatus inProgress,
            @Param("unknown") TossPaymentStatus unknown
    );
}
