package com.demo.matching.payment.domain.toss.exception;

import org.springframework.http.HttpStatus;

public class TossPaymentConfirmException extends RuntimeException {
    private final TossPaymentConfirmErrorCode errorCode;

    public TossPaymentConfirmException(Exception cause) {
        super("결제 승인 중 오류 발생", cause);
        this.errorCode = TossPaymentConfirmErrorCode.PAYMENT_CONFIRM_ERROR_MISMATCH_ERROR;
    }

    // Toss 응답 코드 기반 예외
    public TossPaymentConfirmException(TossPaymentConfirmErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public TossPaymentConfirmErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
