package com.demo.matching.payment.presentation.toss.response;

import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import lombok.Builder;

@Builder
public record TossCheckoutResponse(
        String orderId,
        long amount,
        String orderName,   // 결제 건 이름 ex ) "포인트 충전"
        Long memberId, // 고객 식별용 키
        String successUrl,  // Toss 가 redirect 할 URL
        String failUrl,     // Toss 가 redirect 할 URL, 실패 사유 code, message 파라미터가 함께 옴
        String clientKey    // Toss 에서 발급한 공개용 클라이언트 키
) {
    public static TossCheckoutResponse from(TossPaymentEvent tossPaymentEvent, String successUrl, String failUrl, String clientKey) {
        return TossCheckoutResponse.builder()
                .orderId(tossPaymentEvent.getOrderId())
                .amount(tossPaymentEvent.getAmount())
                .orderName(tossPaymentEvent.getOrderName())
                .memberId(tossPaymentEvent.getBuyerId())
                .successUrl(successUrl)
                .failUrl(failUrl)
                .clientKey(clientKey)
                .build();
    }
}
