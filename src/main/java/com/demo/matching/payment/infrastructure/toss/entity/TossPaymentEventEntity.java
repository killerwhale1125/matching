package com.demo.matching.payment.infrastructure.toss.entity;

import com.demo.matching.core.common.infrastructure.BaseTimeEntity;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.enums.TossPaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class TossPaymentEventEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_event_id")
    private Long id;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "amount")
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TossPaymentStatus tossPaymentStatus;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "retry_count")
    private Integer retryCount;

    public static TossPaymentEventEntity from(TossPaymentEvent tossPaymentEvent) {
        TossPaymentEventEntity entity = new TossPaymentEventEntity();
        entity.id = tossPaymentEvent.getId();
        entity.buyerId = tossPaymentEvent.getBuyerId();
        entity.orderName = tossPaymentEvent.getOrderName();
        entity.orderId = tossPaymentEvent.getOrderId();
        entity.paymentKey = tossPaymentEvent.getPaymentKey();
        entity.amount = tossPaymentEvent.getAmount();
        entity.tossPaymentStatus = tossPaymentEvent.getTossPaymentStatus();
        entity.executedAt = tossPaymentEvent.getExecutedAt();
        entity.approvedAt = tossPaymentEvent.getApprovedAt();
        entity.retryCount = tossPaymentEvent.getRetryCount();
        entity.createdTime = tossPaymentEvent.getCreatedTime();
        entity.modifiedTime = tossPaymentEvent.getModifiedTime();
        return entity;
    }

    public TossPaymentEvent to() {
        return TossPaymentEvent.builder()
                .id(id)
                .buyerId(buyerId)
                .orderName(orderName)
                .orderId(orderId)
                .paymentKey(paymentKey)
                .amount(amount)
                .tossPaymentStatus(tossPaymentStatus)
                .executedAt(executedAt)
                .approvedAt(approvedAt)
                .retryCount(retryCount)
                .createdTime(createdTime)
                .modifiedTime(modifiedTime)
                .build();
    }
}
