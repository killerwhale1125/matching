package com.demo.matching.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessResponseStatus {
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE_NOT_FOUND", "프로필이 존재하지 않습니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER_ALREADY_EXISTS", "이미 존재하는 회원입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    BusinessResponseStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
