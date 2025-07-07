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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentConfirmErrorCode.ALREADY_PROCESSED_PAYMENT;
import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus.NON_RETRYABLE_ERROR;
import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus.RETRYABLE_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TossPaymentRecoverServiceImplTest {
    private TossPaymentSelectUseCase tossPaymentSelectUseCase;
    private TossPaymentExecutorUseCase tossPaymentExecutorUseCase;
    private TossPaymentFinalizerUseCase tossPaymentFinalizerUseCase;
    private TossApiClientPort tossApiClientPort;
    private LocalDateTimeProvider localDateTimeProvider;

    private TossPaymentRecoverServiceImpl recoverService;

    @BeforeEach
    void setUp() {
        tossPaymentSelectUseCase = mock(TossPaymentSelectUseCase.class);
        tossPaymentExecutorUseCase = mock(TossPaymentExecutorUseCase.class);
        tossPaymentFinalizerUseCase = mock(TossPaymentFinalizerUseCase.class);
        tossApiClientPort = mock(TossApiClientPort.class);
        localDateTimeProvider = mock(LocalDateTimeProvider.class);

        recoverService = new TossPaymentRecoverServiceImpl(
                tossPaymentSelectUseCase,
                tossPaymentExecutorUseCase,
                tossPaymentFinalizerUseCase,
                tossApiClientPort,
                localDateTimeProvider
        );
    }

    @Test
    @DisplayName("성공적으로 결제 재처리 할 수 있다.")
    void retryPaymentEvents_success() {
        // given
        TossPaymentEvent event = mock(TossPaymentEvent.class);
        TossPaymentInfo confirmResult = mock(TossPaymentInfo.class);

        when(tossPaymentSelectUseCase.getRetryablePaymentEvents()).thenReturn(List.of(event));
        when(localDateTimeProvider.now()).thenReturn(LocalDateTime.now());
        when(event.isRetryable(any())).thenReturn(true);
        when(tossApiClientPort.requestConfirm(any())).thenReturn(confirmResult);
        when(confirmResult.tossPaymentConfirmResultStatus()).thenReturn(TossPaymentConfirmResultStatus.SUCCESS);

        // when
        recoverService.retryPaymentEvents();

        // then
        verify(tossPaymentExecutorUseCase).increaseRetryCount(event);
        verify(tossPaymentFinalizerUseCase).finalizeSuccess(event, confirmResult);
    }

    @Test
    @DisplayName("Toss 에서 이미 결제 처리 된 상태이다.")
    void retryPaymentEvents_alreadyProcessed() {
        // given
        TossPaymentEvent event = mock(TossPaymentEvent.class);
        when(tossPaymentSelectUseCase.getRetryablePaymentEvents()).thenReturn(List.of(event));
        when(localDateTimeProvider.now()).thenReturn(LocalDateTime.now());
        when(event.isRetryable(any())).thenReturn(true);

        TossPaymentConfirmException exception = new TossPaymentConfirmException(ALREADY_PROCESSED_PAYMENT);
        when(tossApiClientPort.requestConfirm(any())).thenThrow(exception);

        // when
        recoverService.retryPaymentEvents();

        // then ( 이미 처리된 결제일 경우 호출되는 메소드 )
        verify(tossPaymentFinalizerUseCase).markPaymentAsSuccessIfNotYet(event);
    }

    @Test
    @DisplayName("재시도 가능한 오류 발생하여 UNKNOWN 상태로 변경된다.")
    void retryPaymentEvents_retryableException() {
        // given
        TossPaymentEvent event = mock(TossPaymentEvent.class);
        when(tossPaymentSelectUseCase.getRetryablePaymentEvents()).thenReturn(List.of(event));
        when(localDateTimeProvider.now()).thenReturn(LocalDateTime.now());
        when(event.isRetryable(any())).thenReturn(true);

        TossPaymentException exception = new TossPaymentException(RETRYABLE_ERROR);
        when(tossApiClientPort.requestConfirm(any())).thenThrow(exception);

        // when
        recoverService.retryPaymentEvents();

        // then ( 재시도 가능 상태인 UNKNOWN 상태로 변경 )
        verify(tossPaymentExecutorUseCase).markAsUnknown(event);
    }

    @Test
    @DisplayName("재시도 불가능한 오류 발생으로 최종 실패 처리 된다.")
    void retryPaymentEvents_nonRetryableException() {
        // given
        TossPaymentEvent event = mock(TossPaymentEvent.class);
        TossPaymentException exception = new TossPaymentException(NON_RETRYABLE_ERROR);

        when(tossPaymentSelectUseCase.getRetryablePaymentEvents()).thenReturn(List.of(event));
        when(localDateTimeProvider.now()).thenReturn(LocalDateTime.now());
        when(event.isRetryable(any())).thenReturn(true);
        when(tossApiClientPort.requestConfirm(any())).thenThrow(exception);

        // when
        recoverService.retryPaymentEvents();

        // then ( 재시도 불가능 상태인 FAILED 상태 변경 )
        verify(tossPaymentExecutorUseCase).markAsFail(event);
    }

    @Test
    @DisplayName("결제 이벤트가 재시도 불가능한 경우 Toss API 호출 없이 실패 처리한다.")
    void retryPaymentEvents_notRetryableByTime() {
        // given
        TossPaymentEvent event = mock(TossPaymentEvent.class);

        when(tossPaymentSelectUseCase.getRetryablePaymentEvents()).thenReturn(List.of(event));
        when(localDateTimeProvider.now()).thenReturn(LocalDateTime.now());
        when(event.isRetryable(any())).thenReturn(false);

        // when
        recoverService.retryPaymentEvents();

        // then ( never 로 지정한 메서드 절대 호출되선 안됨 )
        verify(tossApiClientPort, never()).requestConfirm(any());
        verify(tossPaymentExecutorUseCase, never()).increaseRetryCount(event);
        verify(tossPaymentExecutorUseCase, never()).markAsUnknown(event);
        verify(tossPaymentExecutorUseCase).markAsFail(event);
    }
}
