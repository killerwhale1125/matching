package com.demo.matching.common.handler;

import com.demo.matching.common.exception.ApiErrorResponse;
import com.demo.matching.common.exception.BusinessException;
import com.demo.matching.common.exception.BusinessResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex) {
        BusinessResponseStatus status = ex.getStatus();
        return ResponseEntity.status(status.getHttpStatus())
                .body(new ApiErrorResponse(status.getCode(), status.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse("INTERNAL_SERVER_ERROR", "알 수 없는 서버 오류가 발생했습니다."));
    }
}
