package com.demo.matching.payment.domain.toss;

import com.demo.matching.core.common.exception.BusinessException;
import com.demo.matching.payment.domain.toss.exception.TossPaymentException;
import com.demo.matching.payment.domain.toss.enums.TossPaymentStatus;
import com.demo.matching.payment.domain.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.presentation.toss.request.TossCheckoutRequest;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static com.demo.matching.core.common.exception.BusinessResponseStatus.PAYMENT_SUCCESS_BUT_BIZ_FAILED;
import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus.*;
import static com.demo.matching.payment.domain.toss.enums.TossPaymentStatus.*;

@Getter
@Builder
public class TossPaymentEvent {

    /* 3분 후 결제 재시도 */
    public static final int RETRYABLE_MINUTES_FOR_IN_PROGRESS = 3;
    /* 결제 재시도 5번만 가능  */
    public static final int RETRYABLE_LIMIT = 5;

    private Long id;
    private Long buyerId;
    private String orderName;
    private String orderId;
    private String paymentKey;
    private long amount;
    private TossPaymentStatus tossPaymentStatus;
    private LocalDateTime executedAt;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private Integer retryCount;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;

    public static TossPaymentEvent create(String orderId, Long memberId, TossCheckoutRequest request) {
        return TossPaymentEvent.builder()
                .buyerId(memberId)
                .orderName(request.orderName())
                .orderId(orderId)
                .amount(request.amount())
                .tossPaymentStatus(READY)
                .retryCount(0)
                .build();
    }

    public void startConfirmAndUpdate(String paymentKey, long amount, LocalDateTime executedAt) {
        if (isNotExecutableStatus()) {
            throw new TossPaymentException(PAYMENT_INVALID_STATUS_SUCCESS);
        }

        if (amount != this.getAmount()) {
            throw new TossPaymentException(INVALID_AMOUNT);
        }

        markAsInProgress(paymentKey, executedAt);
    }

    public void success(TossPaymentInfo confirmInfo) {
        if (isNotSuccessProcessable()) {
            throw new TossPaymentException(PAYMENT_INVALID_STATUS_SUCCESS);
        }
        this.requestedAt = OffsetDateTime.parse(confirmInfo.requestedAt()).toLocalDateTime();
        this.approvedAt = OffsetDateTime.parse(confirmInfo.approvedAt()).toLocalDateTime();
        this.tossPaymentStatus = DONE;
    }

    /* 재시도가 가능한 상황 */
    public void unknown() {
        /* 해당 상태면 재시도 불가능 */
        if (isNotExecutableStatus()) {
            throw new TossPaymentException(PAYMENT_INVALID_STATUS_UNKNOWN);
        }
        this.tossPaymentStatus = UNKNOWN;
    }

    /* 결제 실패 처리 */
    public void fail() {
        /* 결제를 실패할 수 없는 상황 ( 재시도가 가능한 상황 ) */
        if (isRetryable()) {
            throw new TossPaymentException(INVALID_STATUS_TO_FAIL);
        }
        this.tossPaymentStatus = FAILED;
    }

    public void businessFail() {
        // IN_PROGRESS 상태일 때만 포인트 충전이 안되었다고 판단하여 재시도 로직 가능
        if (this.tossPaymentStatus == IN_PROGRESS) {
            throw new BusinessException(PAYMENT_SUCCESS_BUT_BIZ_FAILED);
        }
        this.tossPaymentStatus = SUCCESS_BUT_BIZ_FAILED;
    }

    public void valid(TossPaymentInfo findResponse, TossConfirmRequest request) {
        if (isNotEqualsOrderId(findResponse.orderId(), request.orderId())) {
            throw new TossPaymentException(INVALID_ORDER_ID);
        }

        if (isNotEqualsPaymentKey(findResponse.paymentKey(), request.paymentKey())) {
            throw new TossPaymentException(INVALID_PAYMENT_KEY);
        }

        if (isNotEqualsAmount(findResponse.totalAmount(), request.amount())) {
            throw new TossPaymentException(INVALID_PAYMENT_KEY);
        }

        if (isNotEqualsBuyerId(request.memberId())) {
            throw new TossPaymentException(INVALID_BUYER);
        }

        if (isValidBeforeConfirmStatus(findResponse.status())) {
            throw new TossPaymentException(NOT_IN_PROGRESS_ORDER);
        }
    }

    /* 결제 요청이 처리중인 상태로 변경 */
    private void markAsInProgress(String paymentKey, LocalDateTime executedAt) {
        this.paymentKey = paymentKey;
        this.tossPaymentStatus = IN_PROGRESS;
        this.executedAt = executedAt;
    }

    private boolean isRetryable() {
        return this.tossPaymentStatus == IN_PROGRESS || this.tossPaymentStatus == UNKNOWN;
    }

    /* 재시도 가능 하고, 재시도 횟수가 5 미만일 경우에는 최종적으로 재시도 가능 */
    public boolean isRetryable(LocalDateTime now) {
        return (isRetryableInProgress(now) || this.tossPaymentStatus == UNKNOWN) &&
                canAttemptRetryCount();
    }

    /* 5분이 지난 IN_PROGRESS 상태이거나 IN_PROGRESS 상태일 경우 재시도 가능 */
    private boolean isRetryableInProgress(LocalDateTime now) {
        return this.executedAt.plusMinutes(RETRYABLE_MINUTES_FOR_IN_PROGRESS).isBefore(now)
                && this.tossPaymentStatus == IN_PROGRESS;
    }

    /* 재시도 횟수 확인 */
    private boolean canAttemptRetryCount() {
        return this.retryCount < RETRYABLE_LIMIT;
    }

    /* 세가지 상태 모두 아닐 경우 결제 진행 불가 ex ) DONE, EXPIRED */
    private boolean isNotExecutableStatus() {
        return !(tossPaymentStatus == READY
                || tossPaymentStatus == IN_PROGRESS
                || tossPaymentStatus == UNKNOWN);
    }

    /* 세가지 상태 모두 아닐 경우 결제 완료 불가 */
    private boolean isNotSuccessProcessable() {
        return !(tossPaymentStatus == DONE
                || tossPaymentStatus == IN_PROGRESS
                || tossPaymentStatus == UNKNOWN);
    }

    private boolean isNotEqualsOrderId(String responseOrderId, String requestOrderId) {
        return !(this.orderId.equals(responseOrderId)
                && this.orderId.equals(requestOrderId)
                && responseOrderId.equals(requestOrderId));
    }

    private boolean isValidBeforeConfirmStatus(String status) {
        TossPaymentStatus tossPaymentStatus = fromString(status);
        return tossPaymentStatus != IN_PROGRESS && tossPaymentStatus != DONE;
    }

    private boolean isNotEqualsBuyerId(long memberId) {
        return this.buyerId != memberId;
    }

    private boolean isNotEqualsAmount(long responseAmount, long requestAmount) {
        boolean allMatch = this.amount == responseAmount
                && this.amount == requestAmount
                && responseAmount == requestAmount;
        return !allMatch;
    }

    private boolean isNotEqualsPaymentKey(String responsePaymentKey, String requestPaymentKey) {
        boolean allMatch = this.paymentKey.equals(responsePaymentKey)
                && this.paymentKey.equals(requestPaymentKey)
                && responsePaymentKey.equals(requestPaymentKey);
        return !allMatch;
    }

    public void increaseRetryCount() {
        if (this.tossPaymentStatus != UNKNOWN &&
                this.tossPaymentStatus != IN_PROGRESS) {
            throw new TossPaymentException(INVALID_STATUS_TO_RETRY);
        }
        retryCount++;
    }
}
