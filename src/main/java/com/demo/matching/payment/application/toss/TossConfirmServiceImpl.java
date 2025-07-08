package com.demo.matching.payment.application.toss;

import com.demo.matching.core.common.exception.BusinessException;
import com.demo.matching.payment.application.toss.port.in.TossApiClientPort;
import com.demo.matching.payment.application.toss.usecase.TossPaymentExecutorUseCase;
import com.demo.matching.payment.application.toss.usecase.TossPaymentFinalizerUseCase;
import com.demo.matching.payment.application.toss.usecase.TossPaymentSelectUseCase;
import com.demo.matching.payment.application.usecase.OrderedMemberUseCase;
import com.demo.matching.payment.domain.toss.exception.TossPaymentConfirmException;
import com.demo.matching.payment.domain.toss.exception.TossPaymentException;
import com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.presentation.port.in.TossConfirmService;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import com.demo.matching.payment.presentation.toss.response.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus.NON_RETRYABLE_ERROR;
import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus.RETRYABLE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossConfirmServiceImpl implements TossConfirmService {
    private final TossPaymentSelectUseCase tossPaymentSelectUseCase;
    private final TossPaymentExecutorUseCase tossPaymentExecutorUseCase;
    private final TossPaymentFinalizerUseCase tossPaymentFinalizerUseCase;
    private final TossApiClientPort tossApiClientPort;
    private final OrderedMemberUseCase orderedMemberUseCase;

    public TossConfirmResponse confirmPayment(TossConfirmRequest request) {
        // 회원 정보 검증
        orderedMemberUseCase.getMemberInfoById(request.memberId());

        // 주문 정보 조회 및 유효성 검사 후 결제 시작 처리 (IN_PROGRESS)
        TossPaymentEvent paymentEvent = tossPaymentSelectUseCase.findPaymentEventByOrderId(request.orderId());
        tossPaymentExecutorUseCase.execute(paymentEvent, request);

        try {
            // Toss 결제 정보 확인 및 멱등성 검증
            TossPaymentInfo tossInfo = tossApiClientPort.findPaymentByPaymentKey(request.paymentKey());
            tossPaymentExecutorUseCase.validateBeforeConfirm(paymentEvent, tossInfo, request);

            // 결제 승인
            TossPaymentInfo confirmedInfo = tossApiClientPort.requestConfirm(request);

            // 결제 최종 처리 (포인트 적립 등)
            tossPaymentFinalizerUseCase.finalizeSuccess(paymentEvent, confirmedInfo);
            return TossConfirmResponse.from(confirmedInfo);
        }

        // Toss API 관련 예외 처리
        catch (TossPaymentConfirmException e) {
            return handleConfirmException(e, paymentEvent);
        }

        // 비즈니스 로직 중 Toss 관련 예외
        catch (TossPaymentException e) {
            return handlePaymentException(e, paymentEvent);
        }

        // 일반 비즈니스 예외
        catch (BusinessException e) {
            tossPaymentExecutorUseCase.markAsSuccessButBusinessFailed(paymentEvent);
            throw wrapException(NON_RETRYABLE_ERROR, e);
        }

        // 예기치 못한 예외
        catch (Exception e) {
            tossPaymentExecutorUseCase.markAsFail(paymentEvent);
            throw e;
        }
    }

    private TossConfirmResponse handleConfirmException(TossPaymentConfirmException e, TossPaymentEvent event) {
        if (e.getErrorCode().isRetryableError()) {
            tossPaymentExecutorUseCase.markAsUnknown(event);
            throw wrapException(RETRYABLE_ERROR, e);
        }
        tossPaymentExecutorUseCase.markAsFail(event);
        throw wrapException(NON_RETRYABLE_ERROR, e);
    }

    private TossConfirmResponse handlePaymentException(TossPaymentException e, TossPaymentEvent event) {
        if (e.getErrorStatus().isRetryableStatus()) {
            tossPaymentExecutorUseCase.markAsUnknown(event);
            throw wrapException(RETRYABLE_ERROR, e);
        }
        tossPaymentExecutorUseCase.markAsFail(event);
        throw wrapException(NON_RETRYABLE_ERROR, e);
    }

    private TossPaymentException wrapException(TossPaymentExceptionStatus status, Throwable cause) {
        return new TossPaymentException(status, cause);
    }
}
