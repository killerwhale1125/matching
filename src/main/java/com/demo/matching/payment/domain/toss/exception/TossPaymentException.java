package com.demo.matching.payment.domain.toss.exception;

import com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus;
import org.springframework.http.HttpStatus;

public class TossPaymentException extends RuntimeException {
    private final TossPaymentExceptionStatus errorStatus;

    // Toss 응답 코드 기반 예외
    public TossPaymentException(TossPaymentExceptionStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public TossPaymentException(TossPaymentExceptionStatus errorStatus, Throwable cause) {
        super(errorStatus.getMessage(), cause);
        this.errorStatus = errorStatus;
    }

    public TossPaymentExceptionStatus getErrorStatus() {
        return errorStatus;
    }

    public HttpStatus getHttpStatus() {
        return errorStatus.getHttpStatus();
    }
}
