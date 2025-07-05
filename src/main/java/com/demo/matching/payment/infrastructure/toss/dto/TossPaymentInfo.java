package com.demo.matching.payment.infrastructure.toss.dto;

import lombok.Builder;

@Builder
public record TossPaymentInfo(
        String paymentKey,  // Toss 에서 발급받은 key ( DB 저장 )
        String orderId, // 주문 id
        String orderName,   // 주문 상품명
        String method,  // 결제수단 ( PaymentMethod )
        long totalAmount,   // 총 결제 금액
        String status,  // 결제 상태 처리 ( TossPaymentStatus )
        String approvedAt   // 결제 승인 날짜
) {}
