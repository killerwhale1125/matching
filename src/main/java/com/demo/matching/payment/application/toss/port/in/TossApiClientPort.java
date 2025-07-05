package com.demo.matching.payment.application.toss.port.in;

import com.demo.matching.payment.infrastructure.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import com.demo.matching.payment.presentation.toss.response.TossConfirmResponse;

public interface TossApiClientPort {
    TossPaymentInfo findPaymentByPaymentKey(String paymentKey);
    TossConfirmResponse requestConfirm(TossConfirmRequest tossConfirmRequest);
}
