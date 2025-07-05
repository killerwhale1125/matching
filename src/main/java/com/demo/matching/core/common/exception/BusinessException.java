package com.demo.matching.core.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BusinessResponseStatus status;

    public BusinessException(BusinessResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
