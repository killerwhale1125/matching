package com.demo.matching.payment.domain.toss.enums;

import lombok.Getter;

@Getter
public enum TossPaymentConfirmResultStatus {
    SUCCESS("SUCCESS"),
    RETRYABLE_FAILURE("RETRYABLE_FAILURE"),
    NON_RETRYABLE_FAILURE("NON_RETRYABLE_FAILURE");

    private final String status;

    TossPaymentConfirmResultStatus(String status) {
        this.status = status;
    }
}
