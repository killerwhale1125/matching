package com.demo.matching.payment.presentation.toss.response;

import com.demo.matching.payment.domain.toss.TossPayment;
import lombok.Builder;

@Builder
public record TossConfirmResponse(
        String paymentKey,  // Toss 에서 발급받은 key ( DB 저장 )
        String orderId, // 주문 id
        String orderName,   // 주문 상품명
        String method,  // 결제수단 ( PaymentMethod )
        long totalAmount,   // 총 결제 금액
        String status,  // 결제 상태 처리 ( TossPaymentStatus )
        String requestedAt, // 결제 발생 날짜
        String approvedAt   // 결제 승인 날짜
) {
    public static TossConfirmResponse from(TossPayment tossPayment) {
        return TossConfirmResponse.builder()
                .paymentKey(tossPayment.getTossPaymentKey())
                .orderId(tossPayment.getOrderId())
                .orderName(tossPayment.getOrderName())
                .method(tossPayment.getTossPaymentMethod().name())
                .totalAmount(tossPayment.getTotalAmount())
                .status(tossPayment.getTossPaymentStatus().name())
                .requestedAt(tossPayment.getRequestedAt().toString())
                .requestedAt(tossPayment.getApprovedAt().toString())
                .build();

    }
}
