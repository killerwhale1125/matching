package com.demo.matching.payment.infrastructure.toss.mapper;

import com.demo.matching.payment.infrastructure.toss.dto.TossConfirmApiResponse;
import com.demo.matching.payment.domain.toss.dto.TossPaymentInfo;

public class TossInfoMapper {
    public static TossPaymentInfo from(TossConfirmApiResponse tossResponse) {
        return TossPaymentInfo.builder()
                .paymentKey(tossResponse.paymentKey())
                .orderId(tossResponse.orderId())
                .orderName(tossResponse.orderName())
                .method(tossResponse.method())
                .totalAmount(tossResponse.totalAmount())
                .status(tossResponse.status())
                .requestedAt(tossResponse.requestedAt())
                .approvedAt(tossResponse.approvedAt())
                .build();
    }
}
