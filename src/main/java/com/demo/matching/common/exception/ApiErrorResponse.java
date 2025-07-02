package com.demo.matching.common.exception;

public class ApiErrorResponse {
    private final String code;
    private final String message;

    public ApiErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(code, message);
    }
}
