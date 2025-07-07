package com.demo.matching.payment.application.toss.usecase;

import com.demo.matching.payment.application.toss.port.in.TossApiClientPort;
import com.demo.matching.payment.application.usecase.PointChargeUseCase;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.dto.TossPaymentInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static com.demo.matching.payment.domain.toss.enums.TossPaymentConfirmResultStatus.SUCCESS;
import static com.demo.matching.payment.domain.toss.enums.TossPaymentStatus.IN_PROGRESS;
import static org.mockito.Mockito.*;

/**
 * ExecutorUseCase 에서 상세 로직 테스트 진행으로 Mock 대체 및 일부 검증 수행
 */
class TossPaymentFinalizerUseCaseTest {

    private TossPaymentExecutorUseCase executorUseCase;
    private PointChargeUseCase pointChargeUseCase;
    private TossApiClientPort apiClientPort;
    private TossPaymentFinalizerUseCase useCase;

    @BeforeEach
    void setUp() {
        executorUseCase = mock(TossPaymentExecutorUseCase.class);
        pointChargeUseCase = mock(PointChargeUseCase.class);
        apiClientPort = mock(TossApiClientPort.class);
        useCase = new TossPaymentFinalizerUseCase(executorUseCase, pointChargeUseCase, apiClientPort);
    }

    @Test
    @DisplayName("결제 성공 처리 및 포인트 충전")
    void finalizeSuccess_success() {
        TossPaymentEvent event = mock(TossPaymentEvent.class);
        TossPaymentInfo info = mock(TossPaymentInfo.class);

        when(event.getBuyerId()).thenReturn(1L);
        when(event.getOrderId()).thenReturn("orderId");
        when(event.getApprovedAt()).thenReturn(LocalDateTime.now());
        when(info.totalAmount()).thenReturn(1000L);

        useCase.finalizeSuccess(event, info);

        verify(executorUseCase).markPaymentAsSuccess(event, info);
        verify(pointChargeUseCase).chargePoint(1L, "orderId", 1000, event.getApprovedAt());
    }

    @Test
    @DisplayName("이미 충전된 경우 아무 동작 하지 않는다.")
    void markAsSuccessIfAlreadyCharged() {
        TossPaymentEvent event = mock(TossPaymentEvent.class);
        when(event.getOrderId()).thenReturn("orderId");
        when(pointChargeUseCase.alreadyCharged("orderId")).thenReturn(true);

        useCase.markPaymentAsSuccessIfNotYet(event);

        verify(pointChargeUseCase).alreadyCharged("orderId");
        verifyNoMoreInteractions(pointChargeUseCase, executorUseCase, apiClientPort);
    }

    @Test
    @DisplayName("포인트 미충전 상태면 결제 정보 재조회 후 복구 및 충전한다.")
    void markAsSuccessIfNotCharged() {
        TossPaymentInfo mockTossPaymentInfo = TossPaymentInfo.builder()
                .paymentKey("paymentKey")
                .orderId("orderId")
                .orderName("orderName")
                .method("간편결제")
                .totalAmount(1000)
                .status("IN_PROGRESS")
                .requestedAt(OffsetDateTime.now().toString())
                .approvedAt(OffsetDateTime.now().toString())
                .tossPaymentConfirmResultStatus(SUCCESS)
                .build();

        TossPaymentEvent mockPaymentEvent = TossPaymentEvent.builder()
                .id(1L)
                .buyerId(1L)
                .orderId("orderId")
                .paymentKey("paymentKey")
                .orderName("orderName")
                .tossPaymentStatus(IN_PROGRESS)
                .amount(1000)
                .executedAt(null)
                .approvedAt(null)
                .requestedAt(null)
                .retryCount(0)
                .build();

        when(pointChargeUseCase.alreadyCharged("orderId")).thenReturn(false);
        when(apiClientPort.findPaymentByPaymentKey("paymentKey")).thenReturn(mockTossPaymentInfo);

        // when
        useCase.markPaymentAsSuccessIfNotYet(mockPaymentEvent);

        // then
        verify(executorUseCase).recoverFromConfirmedPayment(mockPaymentEvent, mockTossPaymentInfo);
        verify(pointChargeUseCase).chargePoint(1L, "orderId", 1000, mockPaymentEvent.getApprovedAt());
    }
}
