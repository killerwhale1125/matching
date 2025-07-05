package com.demo.matching.payment.infrastructure.toss.entity;

import com.demo.matching.core.common.infrastructure.BaseTimeEntity;
import com.demo.matching.payment.domain.toss.TossPayment;
import com.demo.matching.payment.domain.toss.enums.TossPaymentMethod;
import com.demo.matching.payment.domain.toss.enums.TossPaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@Table(name = "toss_payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TossPaymentEntity extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    /* 멤버 식별자만 보유 */
    private Long memberId;

    @Column(unique = true)
    @Comment("Toss 에서 제공하는 결제에 대한 식별 값")
    private String tossPaymentKey;

    @Column(nullable = false)
    @Comment("백엔드 서버에서 생성한 결제 주문 ID")
    private String orderId;

    @Column(nullable = false)
    @Comment("총 결제 금액")
    private long totalAmount;

    @Column(nullable = false)
    @Comment("주문 상품명")
    private String orderName;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TossPaymentMethod tossPaymentMethod;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TossPaymentStatus tossPaymentStatus;

    @Comment("결제 요청 시간")
    private LocalDateTime requestedAt;

    @Comment("결제 승인 시간")
    private LocalDateTime approvedAt;

    public static TossPaymentEntity from(TossPayment tossPayment) {
        TossPaymentEntity tossPaymentEntity = new TossPaymentEntity();
        tossPaymentEntity.id = tossPayment.getId();
        tossPaymentEntity.memberId = tossPayment.getMemberId();
        tossPaymentEntity.tossPaymentKey = tossPayment.getTossPaymentKey();
        tossPaymentEntity.orderId = tossPayment.getOrderId();
        tossPaymentEntity.totalAmount = tossPayment.getTotalAmount();
        tossPaymentEntity.orderName = tossPayment.getOrderName();
        tossPaymentEntity.tossPaymentMethod = tossPayment.getTossPaymentMethod();
        tossPaymentEntity.tossPaymentStatus = tossPayment.getTossPaymentStatus();
        tossPaymentEntity.requestedAt = tossPayment.getRequestedAt();
        tossPaymentEntity.approvedAt = tossPayment.getApprovedAt();
        tossPaymentEntity.createdTime = tossPayment.getCreatedTime();
        tossPaymentEntity.modifiedTime = tossPayment.getModifiedTime();

        return tossPaymentEntity;
    }

    public TossPayment to() {
        return TossPayment.builder()
                .id(id)
                .memberId(memberId)
                .tossPaymentKey(tossPaymentKey)
                .orderId(orderId)
                .totalAmount(totalAmount)
                .orderName(orderName)
                .tossPaymentMethod(tossPaymentMethod)
                .tossPaymentStatus(tossPaymentStatus)
                .requestedAt(requestedAt)
                .approvedAt(approvedAt)
                .createdTime(createdTime)
                .modifiedTime(modifiedTime)
                .build();
    }
}
