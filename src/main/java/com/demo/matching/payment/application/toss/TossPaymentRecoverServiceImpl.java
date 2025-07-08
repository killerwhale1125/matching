package com.demo.matching.payment.application.toss;

import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import com.demo.matching.payment.application.toss.port.in.TossApiClientPort;
import com.demo.matching.payment.application.toss.usecase.TossPaymentExecutorUseCase;
import com.demo.matching.payment.application.toss.usecase.TossPaymentFinalizerUseCase;
import com.demo.matching.payment.application.toss.usecase.TossPaymentSelectUseCase;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.domain.toss.enums.TossPaymentConfirmResultStatus;
import com.demo.matching.payment.domain.toss.exception.TossPaymentConfirmException;
import com.demo.matching.payment.domain.toss.exception.TossPaymentException;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import com.demo.matching.payment.scheduler.port.TossPaymentRecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentConfirmErrorCode.ALREADY_PROCESSED_PAYMENT;
import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus.NON_RETRYABLE_ERROR;
import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus.RETRYABLE_ERROR;

@Service
@RequiredArgsConstructor
public class TossPaymentRecoverServiceImpl implements TossPaymentRecoveryService {
    private final TossPaymentSelectUseCase tossPaymentSelectUseCase;
    private final TossPaymentExecutorUseCase tossPaymentExecutorUseCase;
    private final TossPaymentFinalizerUseCase tossPaymentFinalizerUseCase;
    private final TossApiClientPort tossApiClientPort;
    private final LocalDateTimeProvider localDateTimeProvider;

    public void retryPaymentEvents() {
        List<TossPaymentEvent> retryableEvents = tossPaymentSelectUseCase.getRetryablePaymentEvents();
        retryableEvents.forEach(this::processRetryablePaymentEvent);
    }

    private void processRetryablePaymentEvent(TossPaymentEvent retryableEvent) {
        try {
            /* 재시도 불가능한 상태일 경우 */
            if (!retryableEvent.isRetryable(localDateTimeProvider.now())) {
                throw new TossPaymentException(NON_RETRYABLE_ERROR);
            }
            /* 재시도 횟수 증가 */
            tossPaymentExecutorUseCase.increaseRetryCount(retryableEvent);

            /* 결제 재시도 요청 */
            TossPaymentInfo confirmResult = tossApiClientPort.requestConfirm(createConfirmRequest(retryableEvent));

            /* 결과 상태값 검증 */
            checkResultStatus(confirmResult);

            /* 최종 결제 완료 처리 */
            tossPaymentFinalizerUseCase.finalizeSuccess(retryableEvent, confirmResult);
        } catch (TossPaymentConfirmException e) {
            handleConfirmException(e, retryableEvent);
        } catch (TossPaymentException e) {
            handlePaymentException(e, retryableEvent);
        }
    }

    private TossPaymentInfo checkResultStatus(TossPaymentInfo confirmResult) {
        TossPaymentConfirmResultStatus status = confirmResult.tossPaymentConfirmResultStatus();
        return switch (status) {
            case SUCCESS -> confirmResult;
            case RETRYABLE_FAILURE -> throw new TossPaymentException(RETRYABLE_ERROR);
            case NON_RETRYABLE_FAILURE -> throw new TossPaymentException(NON_RETRYABLE_ERROR);
        };
    }

    private TossConfirmRequest createConfirmRequest(TossPaymentEvent retryableEvent) {
        return TossConfirmRequest.builder()
                .memberId(retryableEvent.getBuyerId())
                .paymentKey(retryableEvent.getPaymentKey())
                .orderId(retryableEvent.getOrderId())
                .amount(retryableEvent.getAmount())
                .build();
    }

    private void handleConfirmException(TossPaymentConfirmException e, TossPaymentEvent paymentEvent) {
        /* 이미 Toss 측 결제 완료 상태 */
        if (e.getErrorCode() == ALREADY_PROCESSED_PAYMENT) {
            tossPaymentFinalizerUseCase.markPaymentAsSuccessIfNotYet(paymentEvent);
            return;
        }

        if (e.getErrorCode().isRetryableError()) {
            tossPaymentExecutorUseCase.markAsUnknown(paymentEvent);
        }
    }

    private void handlePaymentException(TossPaymentException e, TossPaymentEvent paymentEvent) {
        if (e.getErrorStatus().isRetryableStatus()) {
            tossPaymentExecutorUseCase.markAsUnknown(paymentEvent);
            return;
        }
        tossPaymentExecutorUseCase.markAsFail(paymentEvent);
    }

}
