package com.demo.matching.payment.infrastructure.toss;

import com.demo.matching.payment.infrastructure.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.presentation.toss.response.TossConfirmResponse;

public class TossInfoMapper {
    public static TossPaymentInfo from(TossConfirmResponse tossResponse) {
        return TossPaymentInfo.builder()
                .orderId(tossResponse.orderId())
                .totalAmount(tossResponse.totalAmount())
                .approvedAt(tossResponse.approvedAt())
                .status(tossResponse.status())
                .build();
    }
}
