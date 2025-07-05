package com.demo.matching.payment.common.toss.exception.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum TossPaymentConfirmErrorCode {
    ALREADY_PROCESSED_PAYMENT(HttpStatus.BAD_REQUEST, "이미 처리된 결제입니다."),
    PROVIDER_ERROR(HttpStatus.BAD_REQUEST, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_API_KEY(HttpStatus.BAD_REQUEST, "잘못된 시크릿 키 또는 정보입니다."),
    INVALID_EXCEED_MAX_DAILY_PAYMENT_COUNT(HttpStatus.BAD_REQUEST, "하루 결제 횟수를 초과했습니다."),
    EXCEED_MAX_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "허용 최대 금액을 초과했습니다."),
    NOT_FOUND_TERMINAL_ID(HttpStatus.BAD_REQUEST, "Terminal ID가 존재하지 않습니다. 관리자에게 문의해주세요."),
    UNAPPROVED_ORDER_ID(HttpStatus.BAD_REQUEST, "아직 승인되지 않은 주문번호입니다."),
    UNAUTHORIZED_KEY(HttpStatus.BAD_REQUEST, "인증되지 않은 시크릿 키 또는 클라이언트 키입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "결제 비밀번호가 올바르지 않습니다."),
    NOT_FOUND_PAYMENT(HttpStatus.BAD_REQUEST, "존재하지 않는 결제 정보입니다."),
    NOT_FOUND_PAYMENT_SESSION(HttpStatus.BAD_REQUEST, "결제 요청을 처리할 결제 세션이 존재하지 않습니다."),
    NOT_FOUND_MERCHANT(HttpStatus.NOT_FOUND, "존재하지 않는 상점 정보입니다."),

    INVALID_AUTHORIZE_AUTH(HttpStatus.BAD_GATEWAY, "유효하지 않은 인증 방식입니다."),
    NOT_AVAILABLE_PAYMENT(HttpStatus.BAD_GATEWAY, "결제가 불가능한 시간대입니다."),
    INCORRECT_BASIC_AUTH_FORMAT(HttpStatus.BAD_GATEWAY, "인증 정보 형식이 잘못되었습니다."),
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING(HttpStatus.BAD_GATEWAY, "결제가 완료되지 않았습니다. 관리자에게 문의해주세요."),
    FAILED_INTERNAL_SYSTEM_PROCESSING(HttpStatus.BAD_GATEWAY, "내부 시스템 처리 작업이 실패했습니다."),
    UNKNOWN_PAYMENT_ERROR(HttpStatus.BAD_GATEWAY, "결제에 실패했어요. 같은 문제가 반복되면 운영팀에 문의해주세요."),
    UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 에러입니다."),
    NETWORK_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요."), // 타임아웃 처리를 위한 별도로 추가한 에러

    /* 해당 객체의 에러코드와, 토스 객체의 에러 코드명이 일치하지 않을 경우 발생시키기 위한 에러코드 */
    PAYMENT_CONFIRM_ERROR_MISMATCH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "결제 과정에서 서버 예외가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    TossPaymentConfirmErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    /* 토스 결제 결과가 성공인지, 재시도 가능한 에러인지, 실패인지를 판단 */
    public boolean isRetryableError() {
        return switch (this) {
            case PROVIDER_ERROR, FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING,
                 FAILED_INTERNAL_SYSTEM_PROCESSING, UNKNOWN_PAYMENT_ERROR,
                 UNKNOWN, NETWORK_ERROR -> true;
            default -> false;
        };
    }

    public static TossPaymentConfirmErrorCode findByName(String name) {
        return Arrays.stream(values())
                .filter(v -> v.name().equals(name))
                .findFirst()
                .orElse(PAYMENT_CONFIRM_ERROR_MISMATCH_ERROR);
    }

    public boolean isSuccess() {
        return this == ALREADY_PROCESSED_PAYMENT;
    }

    public boolean isFailure() {
        return !isSuccess() && !isRetryableError();
    }
}
