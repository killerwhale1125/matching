package com.demo.matching.payment.application.toss;

import com.demo.matching.core.common.exception.BusinessException;
import com.demo.matching.payment.application.toss.port.in.TossApiClientPort;
import com.demo.matching.payment.application.toss.usecase.TossPaymentExecutorUseCase;
import com.demo.matching.payment.application.toss.usecase.TossPaymentFinalizerUseCase;
import com.demo.matching.payment.application.toss.usecase.TossPaymentSelectUseCase;
import com.demo.matching.payment.application.usecase.OrderedMemberUseCase;
import com.demo.matching.payment.domain.toss.exception.TossPaymentConfirmException;
import com.demo.matching.payment.domain.toss.exception.TossPaymentException;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import com.demo.matching.payment.presentation.toss.response.TossConfirmResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.demo.matching.core.common.exception.BusinessResponseStatus.MEMBER_NOT_FOUND;
import static com.demo.matching.core.common.exception.BusinessResponseStatus.PAYMENT_SUCCESS_BUT_BIZ_FAILED;
import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentConfirmErrorCode.*;
import static com.demo.matching.payment.common.toss.exception.enums.TossPaymentExceptionStatus.INVALID_PAYMENT_KEY;
import static com.demo.matching.payment.domain.toss.enums.TossPaymentConfirmResultStatus.SUCCESS;
import static com.demo.matching.payment.domain.toss.enums.TossPaymentStatus.IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class TossConfirmServiceImplTest {
    private TossPaymentSelectUseCase selectUseCase;
    private TossPaymentExecutorUseCase executorUseCase;
    private TossPaymentFinalizerUseCase finalizerUseCase;
    private TossApiClientPort apiClientPort;
    private OrderedMemberUseCase memberUseCase;
    private TossConfirmServiceImpl service;

    @Test
    @DisplayName("회원 정보 조회 실패 시 예외 발생")
    void confirmPayment_memberInfoFetch_fail() {
        // given
        TossConfirmRequest request = new TossConfirmRequest(1L, "paymentKey", "orderId", 1000);
        doThrow(new BusinessException(MEMBER_NOT_FOUND))
                .when(memberUseCase).getMemberInfoById(1L);

        // when
        assertThatThrownBy(() -> service.confirmPayment(request))
                .isInstanceOf(BusinessException.class);

        // then
        verifyNoInteractions(selectUseCase, apiClientPort, finalizerUseCase);
    }

    @Test
    @DisplayName("결제 승인 성공")
    void confirmPayment_success() {
        // given
        MockConfirmData mockConfirmData = getDefaultMockConfirmData();
        TossConfirmRequest request = mockConfirmData.mockTossConfirmRequest;
        TossPaymentEvent event = mockConfirmData.mockPaymentEvent;
        TossPaymentInfo info = mockConfirmData.mockTossPaymentInfo;

        when(selectUseCase.findPaymentEventByOrderId("orderId")).thenReturn(event);
        when(apiClientPort.findPaymentByPaymentKey("paymentKey")).thenReturn(info);
        when(apiClientPort.requestConfirm(request)).thenReturn(info);

        // when
        TossConfirmResponse result = service.confirmPayment(request);

        // then
        assertThat(result).isNotNull();
        verify(finalizerUseCase).finalizeSuccess(event, info);
    }

    @Test
    @DisplayName("Toss 서버 승인 요청 시 재시도 가능 에러가 발생하여 재시도 마킹이 진행된다.")
    void confirmPayment_confirmException_retryable() {
        // given
        MockConfirmData mockConfirmData = getDefaultMockConfirmData();
        TossConfirmRequest request = mockConfirmData.mockTossConfirmRequest;
        TossPaymentEvent event = mockConfirmData.mockPaymentEvent;
        TossPaymentConfirmException ex = new TossPaymentConfirmException(UNKNOWN);

        when(selectUseCase.findPaymentEventByOrderId(any())).thenReturn(event);
        when(apiClientPort.findPaymentByPaymentKey(any())).thenThrow(ex);

        // when & then
        assertThatThrownBy(() -> service.confirmPayment(request))
                .isInstanceOf(TossPaymentException.class);
        verify(executorUseCase).markAsUnknown(event);
    }

    @Test
    @DisplayName("Toss 서버 승인 요청 시 재시도 블가능 에러가 발생하여 실패 처리 된다")
    void confirmPayment_paymentException_nonRetryable() {
        // given
        MockConfirmData mockConfirmData = getDefaultMockConfirmData();
        TossConfirmRequest request = mockConfirmData.mockTossConfirmRequest;
        TossPaymentEvent event = mockConfirmData.mockPaymentEvent;
        TossPaymentInfo info = mockConfirmData.mockTossPaymentInfo;
        TossPaymentConfirmException ex = new TossPaymentConfirmException(ALREADY_PROCESSED_PAYMENT);

        when(selectUseCase.findPaymentEventByOrderId(any())).thenReturn(event);
        when(apiClientPort.findPaymentByPaymentKey(any())).thenReturn(info);
        when(apiClientPort.requestConfirm(any())).thenThrow(ex);

        // when & then
        assertThatThrownBy(() -> service.confirmPayment(request))
                .isInstanceOf(TossPaymentException.class);
        verify(executorUseCase).markAsFail(event);
    }

    @Test
    @DisplayName("비즈니스 로직 예외 발생 시 markAsSuccessButBusinessFailed 호출")
    void confirmPayment_businessException() {
        MockConfirmData mockConfirmData = getDefaultMockConfirmData();
        TossConfirmRequest request = mockConfirmData.mockTossConfirmRequest;
        TossPaymentEvent event = mockConfirmData.mockPaymentEvent;
        TossPaymentInfo info = mockConfirmData.mockTossPaymentInfo;

        when(selectUseCase.findPaymentEventByOrderId(any())).thenReturn(event);
        when(apiClientPort.findPaymentByPaymentKey(any())).thenReturn(info);
        when(apiClientPort.requestConfirm(any())).thenThrow(new BusinessException(PAYMENT_SUCCESS_BUT_BIZ_FAILED));

        assertThatThrownBy(() -> service.confirmPayment(request))
                .isInstanceOf(TossPaymentException.class);

        verify(executorUseCase).markAsSuccessButBusinessFailed(event);
    }

    @Test
    @DisplayName("validateBeforeConfirm 멱등성 검증 시 예외 발생 경우 markAsFail 호출")
    void confirmPayment_validateBeforeConfirm_throws() {
        MockConfirmData mock = getDefaultMockConfirmData();
        TossConfirmRequest request = mock.mockTossConfirmRequest;
        TossPaymentEvent event = mock.mockPaymentEvent;
        TossPaymentInfo info = mock.mockTossPaymentInfo;

        when(selectUseCase.findPaymentEventByOrderId("orderId")).thenReturn(event);
        when(apiClientPort.findPaymentByPaymentKey("paymentKey")).thenReturn(info);
        doThrow(new TossPaymentException(INVALID_PAYMENT_KEY))
                .when(executorUseCase).validateBeforeConfirm(event, info, request);

        assertThatThrownBy(() -> service.confirmPayment(request))
                .isInstanceOf(TossPaymentException.class);

        verify(executorUseCase).markAsFail(event);
    }

    @Test
    @DisplayName("기타 예외 발생 시 markAsFail 호출")
    void confirmPayment_unexpectedException() {
        MockConfirmData mockConfirmData = getDefaultMockConfirmData();
        TossConfirmRequest request = mockConfirmData.mockTossConfirmRequest;
        TossPaymentEvent event = mockConfirmData.mockPaymentEvent;

        when(selectUseCase.findPaymentEventByOrderId(any())).thenReturn(event);
        when(apiClientPort.findPaymentByPaymentKey(any())).thenThrow(new TossPaymentConfirmException(NOT_FOUND_PAYMENT));

        assertThatThrownBy(() -> service.confirmPayment(request))
                .isInstanceOf(RuntimeException.class);

        verify(executorUseCase).markAsFail(event);
    }

    private static MockConfirmData getDefaultMockConfirmData() {
        TossPaymentInfo mockTossPaymentInfo = TossPaymentInfo.builder()
                .paymentKey("paymentKey")
                .orderId("orderId")
                .orderName("orderName")
                .method("method")
                .totalAmount(1000)
                .status("status")
                .requestedAt(null)
                .approvedAt(null)
                .tossPaymentConfirmResultStatus(SUCCESS)
                .build();

        TossPaymentEvent mockPaymentEvent = TossPaymentEvent.builder()
                .id(1L)
                .buyerId(1L)
                .orderId("order123")
                .paymentKey("paymentKey")
                .orderName("orderName")
                .amount(1000)
                .tossPaymentStatus(IN_PROGRESS)
                .executedAt(null)
                .approvedAt(null)
                .requestedAt(null)
                .build();

        TossConfirmRequest mockTossConfirmRequest = new TossConfirmRequest(1L, "paymentKey", "orderId", 1000);

        return new MockConfirmData(mockTossPaymentInfo, mockPaymentEvent, mockTossConfirmRequest);
    }

    @BeforeEach
    void setUp() {
        selectUseCase = mock(TossPaymentSelectUseCase.class);
        executorUseCase = mock(TossPaymentExecutorUseCase.class);
        finalizerUseCase = mock(TossPaymentFinalizerUseCase.class);
        apiClientPort = mock(TossApiClientPort.class);
        memberUseCase = mock(OrderedMemberUseCase.class);
        service = new TossConfirmServiceImpl(selectUseCase, executorUseCase, finalizerUseCase, apiClientPort, memberUseCase);
    }

    private record MockConfirmData(TossPaymentInfo mockTossPaymentInfo, TossPaymentEvent mockPaymentEvent, TossConfirmRequest mockTossConfirmRequest) {

    }
}
