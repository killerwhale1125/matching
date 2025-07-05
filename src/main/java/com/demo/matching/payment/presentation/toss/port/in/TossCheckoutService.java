package com.demo.matching.payment.presentation.toss.port.in;

import com.demo.matching.payment.presentation.toss.request.TossCheckoutRequest;
import com.demo.matching.payment.presentation.toss.response.TossCheckoutResponse;

public interface TossCheckoutService {
    TossCheckoutResponse checkoutPayment(TossCheckoutRequest request);
}
