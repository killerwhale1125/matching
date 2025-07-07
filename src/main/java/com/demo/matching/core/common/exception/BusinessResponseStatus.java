package com.demo.matching.core.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessResponseStatus {
    SUCCESS(HttpStatus.OK, "SUCCESS", "요청에 성공하였습니다."),
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE_NOT_FOUND", "프로필이 존재하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원이 존재하지 않습니다."),
    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "POINT_NOT_FOUND", "포인트가 존재하지 않습니다."),
    INVALID_POINT_AMOUNT(HttpStatus.BAD_REQUEST, "INVALID_POINT_AMOUNT", "음수 값은 충전할 수 없습니다."),
    PAYMENT_SUCCESS_BUT_BIZ_FAILED(HttpStatus.CONFLICT, "PAYMENT_SUCCESS_BUT_BIZ_FAILED", "결제는 성공했지만 비즈니스 로직이 실패했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "테스트 전용 상태");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    BusinessResponseStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
