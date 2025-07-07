package com.demo.matching.payment.presentation.port.in;

import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import com.demo.matching.payment.presentation.toss.response.TossConfirmResponse;

public interface TossConfirmService {
    TossConfirmResponse confirmPayment(TossConfirmRequest request);
}
