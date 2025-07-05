package com.demo.matching.payment.application.toss.port.in;

import com.demo.matching.payment.domain.toss.TossPayment;

public interface TossPaymentRepository {
    TossPayment save(TossPayment tossPayment);
}
