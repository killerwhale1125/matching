package com.demo.matching.payment.domain.toss.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TossPaymentExceptionStatus {
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_NOT_FOUND", "결제 정보가 존재하지 않습니다."),
    PAYMENT_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_EVENT_NOT_FOUND", "결제 요청 정보가 존재하지 않습니다."),
    PAYMENT_INVALID_STATUS_SUCCESS(HttpStatus.CONFLICT, "PAYMENT_INVALID_STATUS_SUCCESS", "결제를 완료할 수 없는 상태입니다."),
    PAYMENT_INVALID_STATUS_UNKNOWN(HttpStatus.CONFLICT, "PAYMENT_INVALID_STATUS_UNKNOWN", "알 수 없는 상태로 결제를 진행할 수 없습니다."),
    RETRYABLE_ERROR(HttpStatus.CONFLICT, "NON_RETRYABLE_ERROR","Toss 결제에서 재시도 가능한 오류가 발생했습니다."),
    NON_RETRYABLE_ERROR(HttpStatus.CONFLICT, "NON_RETRYABLE_ERROR","Toss 결제에서 재시도 불가능한 오류가 발생했습니다."),
    NOT_IN_PROGRESS_ORDER(HttpStatus.CONFLICT, "NOT_IN_PROGRESS_ORDER","진행 중인 주문이 아닙니다."),
    INVALID_STATUS_TO_FAIL(HttpStatus.CONFLICT, "INVALID_STATUS_TO_FAIL", "결제를 실패할 수 없는 상태입니다."),
    INVALID_ORDER_ID(HttpStatus.CONFLICT, "INVALID_ORDER_ID", "유효하지 않은 주문 번호입니다."),
    INVALID_PAYMENT_KEY(HttpStatus.CONFLICT, "INVALID_PAYMENT_KEY", "유효하지 않은 결제키 입니다."),
    INVALID_BUYER(HttpStatus.CONFLICT, "INVALID_BUYER", "구매자 정보가 일치하지 않습니다."),
    INVALID_AMOUNT(HttpStatus.CONFLICT, "INVALID_AMOUNT", "결제 금액이 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    TossPaymentExceptionStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
