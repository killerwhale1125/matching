package com.demo.matching.payment.application.toss.port.in;

import com.demo.matching.payment.infrastructure.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;

public interface TossApiClientPort {
    TossPaymentInfo findPaymentByPaymentKey(String paymentKey);
    TossPaymentInfo requestConfirm(TossConfirmRequest tossConfirmRequest);
}
