package com.demo.matching.payment.common.toss.exception;

import java.io.IOException;

public class TossPaymentTimeoutException extends RuntimeException {
    public TossPaymentTimeoutException(IOException message) {
        super("결제 승인 타임아웃 발생", message);
    }
}
