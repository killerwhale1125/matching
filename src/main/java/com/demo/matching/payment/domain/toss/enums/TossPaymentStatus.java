package com.demo.matching.payment.domain.toss.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TossPaymentStatus {
    ABORTED("ABORTED"), // 결제 승인 실패
    CANCELED("CANCELED"), // 결제가 취소된 상태
    DONE("DONE"), // 결제가 성공적으로 완료된 상태
    EXPIRED("EXPIRED"), // 결제 만료됨
    IN_PROGRESS("IN_PROGRESS"), // 결제 승인 요청이 처리 중인 상태
    PARTIAL_CANCELED("PARTIAL_CANCELED"), // 결제 부분 취소
    READY("READY"), // 결제 인증 전 초기 상태
    FAILED("FAILED"), // 결제 실패
    SUCCESS_BUT_BIZ_FAILED("SUCCESS_BUT_BIZ_FAILED"), // 결제 승인 완료 및 비즈니스 로직 실패 상태
    /* 모든 에러를 바로 실패 처리하지 않고 재시도를 통해 결제 성공으로 이어질 수 있도록 해야함 */
    UNKNOWN("UNKNOWN"), // 에러가 발생하였으나, 재시도를 통해 해결 가능성이 있는 상태
    WAITING_FOR_DEPOSIT("WAITING_FOR_DEPOSIT"); // 입금 대

    private final String status;

    TossPaymentStatus(String status) {
        this.status = status;
    }

    public static TossPaymentStatus fromString(String status) {
        return Arrays.stream(values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + status));
    }
}