package com.demo.matching.payment.domain.toss;

import com.demo.matching.payment.domain.toss.enums.TossPaymentMethod;
import com.demo.matching.payment.domain.toss.enums.TossPaymentStatus;
import com.demo.matching.payment.infrastructure.toss.dto.TossPaymentInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Builder
public class TossPayment {
    private Long id;
    private Long memberId;
    private String tossPaymentKey;
    private String orderId;
    private long totalAmount;
    private String orderName;
    private TossPaymentMethod tossPaymentMethod;
    private TossPaymentStatus tossPaymentStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;

    public static TossPayment create(Long buyerId, String orderName, TossPaymentInfo response) {
        return TossPayment.builder()
                .memberId(buyerId)
                .tossPaymentKey(response.paymentKey())
                .orderId(response.orderId())
                .totalAmount(response.totalAmount())
                .orderName(orderName)
                .tossPaymentMethod(TossPaymentMethod.fromDisplayName(response.method()))
                .tossPaymentStatus(TossPaymentStatus.fromString(response.status()))
                .requestedAt(OffsetDateTime.parse(response.requestedAt()).toLocalDateTime())
                .approvedAt(OffsetDateTime.parse(response.approvedAt()).toLocalDateTime())
                .build();
    }
}
