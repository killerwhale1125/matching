package com.demo.matching.payment.common.toss.exception;

import com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus;
import lombok.Getter;

import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus.SUCCESS;

@Getter
public class PaymentResponse<T> {
    private final int status;
    private final String message;
    private final String code;
    private T result;

    public PaymentResponse() {
        this.status = SUCCESS.getHttpStatus().value();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
    }

    /**
     * 요청 성공
     * @param result
     */
    public PaymentResponse(T result) {
        this.status = SUCCESS.getHttpStatus().value();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
        this.result = result;
    }

    /**
     * 요청 실패 - Enum 전용
     */
    public PaymentResponse(TossPaymentExceptionStatus status) {
        this.status = status.getHttpStatus().value();
        this.message = status.getMessage();
        this.code = status.getCode();
    }

    /**
     * 요청 실패 - Enum + @Valid 검증 메세지 출력 전용
     */
    public PaymentResponse(TossPaymentExceptionStatus status, String errorMessage) {
        this.status = status.getHttpStatus().value();
        this.message = errorMessage;
        this.code = status.getCode();
    }
}
