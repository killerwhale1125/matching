package com.demo.matching.payment.infrastructure.toss;

import com.demo.matching.payment.infrastructure.toss.dto.TossConfirmApiResponse;
import com.demo.matching.payment.infrastructure.toss.dto.TossPaymentInfo;

public class TossInfoMapper {
    public static TossPaymentInfo from(TossConfirmApiResponse tossResponse) {
        return TossPaymentInfo.builder()
                .orderId(tossResponse.orderId())
                .totalAmount(tossResponse.totalAmount())
                .approvedAt(tossResponse.approvedAt())
                .status(tossResponse.status())
                .build();
    }
}
