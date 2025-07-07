package com.demo.matching.payment.scheduler.port;

public interface TossPaymentRecoveryService {
    void retryPaymentEvents();
}
