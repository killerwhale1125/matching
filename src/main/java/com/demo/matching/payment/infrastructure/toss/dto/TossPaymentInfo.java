package com.demo.matching.payment.infrastructure.toss.dto;

import com.demo.matching.payment.domain.toss.enums.TossPaymentConfirmResultStatus;
import lombok.Builder;

import static com.demo.matching.payment.domain.toss.enums.TossPaymentConfirmResultStatus.SUCCESS;

@Builder
public record TossPaymentInfo(
        String paymentKey,  // Toss 에서 발급받은 key ( DB 저장 )
        String orderId, // 주문 id
        String orderName,   // 주문 상품명
        String method,  // 결제수단 ( PaymentMethod )
        long totalAmount,   // 총 결제 금액
        String status,  // 결제 상태 처리 ( TossPaymentStatus )
        String requestedAt,
        String approvedAt,   // 결제 승인 날짜
        TossPaymentConfirmResultStatus tossPaymentConfirmResultStatus

) {
    public static TossPaymentInfo from(TossConfirmApiResponse tossConfirmApiResponse) {
        return TossPaymentInfo.builder()
                .paymentKey(tossConfirmApiResponse.paymentKey())
                .orderId(tossConfirmApiResponse.orderId())
                .orderName(tossConfirmApiResponse.orderName())
                .method(tossConfirmApiResponse.method())
                .totalAmount(tossConfirmApiResponse.totalAmount())
                .status(tossConfirmApiResponse.status())
                .requestedAt(tossConfirmApiResponse.requestedAt())
                .approvedAt(tossConfirmApiResponse.approvedAt())
                .tossPaymentConfirmResultStatus(SUCCESS)
                .build();
    }
}
