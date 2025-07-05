package com.demo.matching.core.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Enum Status 전용 Handler
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BusinessResponse<BusinessResponseStatus>> handleBusiness(BusinessException e) {
        BusinessResponse<BusinessResponseStatus> response = new BusinessResponse<>(e.getStatus());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * @Valid 전용 Handler
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<BusinessResponse<BusinessResponseStatus>> handleBindException(BindException e) {
        String errorMessage = Optional.ofNullable(e.getBindingResult().getFieldError())
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("요청 값이 올바르지 않습니다.");

        BusinessResponseStatus status = BusinessResponseStatus.BAD_REQUEST;
        BusinessResponse<BusinessResponseStatus> response = new BusinessResponse<>(status, errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
