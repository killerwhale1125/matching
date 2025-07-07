package com.demo.matching.payment.presentation.toss.handler;

import com.demo.matching.payment.presentation.toss.response.PaymentResponse;
import com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus;
import com.demo.matching.payment.domain.toss.exception.TossPaymentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TossPaymentExceptionHandler {
    @ExceptionHandler(TossPaymentException.class)
    public ResponseEntity<PaymentResponse<TossPaymentExceptionStatus>> handleTossPaymentException(TossPaymentException ex) {
        PaymentResponse<TossPaymentExceptionStatus> response = new PaymentResponse<>(ex.getErrorStatus());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
