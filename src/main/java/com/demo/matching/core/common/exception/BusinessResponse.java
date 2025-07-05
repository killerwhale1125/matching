package com.demo.matching.core.common.exception;

import lombok.Getter;

import static com.demo.matching.core.common.exception.BusinessResponseStatus.SUCCESS;

@Getter
public class BusinessResponse<T> {
    private final int status;
    private final String message;
    private final String code;
    private T result;

    public BusinessResponse() {
        this.status = SUCCESS.getHttpStatus().value();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
    }

    /**
     * 요청 성공
     * @param result
     */
    public BusinessResponse(T result) {
        this.status = SUCCESS.getHttpStatus().value();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
        this.result = result;
    }

    /**
     * 요청 실패 - Enum 전용
     */
    public BusinessResponse(BusinessResponseStatus status) {
        this.status = status.getHttpStatus().value();
        this.message = status.getMessage();
        this.code = status.getCode();
    }

    /**
     * 요청 실패 - Enum + @Valid 검증 메세지 출력 전용
     */
    public BusinessResponse(BusinessResponseStatus status, String errorMessage) {
        this.status = status.getHttpStatus().value();
        this.message = errorMessage;
        this.code = status.getCode();
    }
}
