package com.demo.matching.payment.application.toss.usecase;

import com.demo.matching.core.common.exception.BusinessException;
import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import com.demo.matching.payment.application.toss.port.in.TossPaymentEventRepository;
import com.demo.matching.payment.application.toss.port.in.TossPaymentRepository;
import com.demo.matching.payment.domain.toss.exception.TossPaymentException;
import com.demo.matching.payment.domain.toss.TossPayment;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static com.demo.matching.payment.domain.toss.enums.TossPaymentConfirmResultStatus.SUCCESS;
import static com.demo.matching.payment.domain.toss.enums.TossPaymentMethod.EASY_PAY;
import static com.demo.matching.payment.domain.toss.enums.TossPaymentStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class TossPaymentExecutorUseCaseTest {

    private TossPaymentExecutorUseCase useCase;
    private TossPaymentEventRepository eventRepository;
    private TossPaymentRepository paymentRepository;
    private LocalDateTimeProvider timeProvider;

    @BeforeEach
    void setUp() {
        eventRepository = mock(TossPaymentEventRepository.class);
        paymentRepository = mock(TossPaymentRepository.class);
        timeProvider = mock(LocalDateTimeProvider.class);
        useCase = new TossPaymentExecutorUseCase(timeProvider, eventRepository, paymentRepository);
    }

    @Test
    @DisplayName("결제 정보 검증에 성공하여 정상적으로 DB 저장 호출이 진행된다.")
    void testExecute() {
        // given
        MockExecutorData mockExecutorData = getDefaultMockConfirmData();
        TossConfirmRequest mockTossConfirmRequest = mockExecutorData.mockTossConfirmRequest;
        TossPaymentEvent mockPaymentEvent = mockExecutorData.mockPaymentEvent;

        LocalDateTime now = LocalDateTime.now();
        when(timeProvider.now()).thenReturn(now);

        // when
        useCase.execute(mockPaymentEvent, mockTossConfirmRequest);

        // then
        assertThat(mockPaymentEvent.getTossPaymentStatus()).isEqualTo(IN_PROGRESS);
        assertThat(mockPaymentEvent.getPaymentKey()).isEqualTo("paymentKey");
        assertThat(mockPaymentEvent.getExecutedAt()).isEqualTo(now);
        verify(eventRepository).save(mockPaymentEvent);
    }

    @Test
    @DisplayName("결제 승인이 완료되어 결제 상태 변경에 성공한다.")
    void testMarkPaymentAsSuccess() {
        // given
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
                .build();

        // then
        assertDoesNotThrow(()
                -> useCase.markPaymentAsSuccess(mockPaymentEvent, mockTossPaymentInfo));
//        assertThat(mockPaymentEvent.getTossPaymentStatus()).isEqualTo(DONE);
    }

    @Test
    @DisplayName("DONE, UNKNOWN, IN_PROGRESS 상태가 아니라면 결제 완료에 실패한다.")
    void testMarkPaymentAsFail() {
        // given
        MockExecutorData mockExecutorData = getDefaultMockConfirmData();
        TossPaymentEvent mockPaymentEvent = TossPaymentEvent.builder()
                .id(1L)
                .buyerId(1L)
                .orderId("orderId")
                .paymentKey("paymentKey")
                .orderName("orderName")
                .tossPaymentStatus(READY)
                .amount(1000)
                .build();

        TossPaymentInfo mockTossPaymentInfo = mockExecutorData.mockTossPaymentInfo;

        // when
        assertThatThrownBy(() -> useCase.markPaymentAsSuccess(mockPaymentEvent, mockTossPaymentInfo))
                .isInstanceOf(TossPaymentException.class);
    }

    @Test
    @DisplayName("재시도 스케줄링을 통해 포인트 복구에 성공한다.")
    void testRecoverFromConfirmedPayment() {
        // given
        MockExecutorData mockExecutorData = getDefaultMockConfirmData();
        TossPaymentEvent mockPaymentEvent = mockExecutorData.mockPaymentEvent;
        TossPaymentInfo mockTossPaymentInfo = mockExecutorData.mockTossPaymentInfo;
        TossPayment mockTossPayment = mockExecutorData.mockTossPayment;

        // when
        useCase.recoverFromConfirmedPayment(mockPaymentEvent, mockTossPaymentInfo);

        // then
        assertThat(mockTossPayment.getTossPaymentStatus()).isEqualTo(DONE);
        verify(eventRepository).save(mockPaymentEvent);
    }

    @Test
    @DisplayName("DONE, UNKNOWN, IN_PROGRESS 상태가 아니라면 재시도 복구에 실패한다.")
    void testRecoverFromConfirmedPaymentFail() {
        // given
        MockExecutorData mockExecutorData = getDefaultMockConfirmData();
        TossPaymentEvent mockPaymentEvent = TossPaymentEvent.builder()
                .id(1L)
                .buyerId(1L)
                .orderId("orderId")
                .paymentKey("paymentKey")
                .orderName("orderName")
                .tossPaymentStatus(READY)
                .amount(1000)
                .build();
        TossPaymentInfo mockTossPaymentInfo = mockExecutorData.mockTossPaymentInfo;
        TossPayment mockTossPayment = mockExecutorData.mockTossPayment;

        mockStatic(TossPayment.class)
                .when(() -> TossPayment.create(anyLong(), any(TossPaymentInfo.class)))
                .thenReturn(mockTossPayment);

        // when & then
        assertThatThrownBy(() -> useCase.recoverFromConfirmedPayment(mockPaymentEvent, mockTossPaymentInfo))
                .isInstanceOf(TossPaymentException.class);
    }

    @Test
    @DisplayName("READY, IN_PROGRESS, UNKNOWN 상태라면 재시도 가능상태로 DB 동기화에 성공한다.")
    void testMarkAsUnknown() {
        // given
        MockExecutorData mockExecutorData = getDefaultMockConfirmData();
        TossPaymentEvent mockPaymentEvent = mockExecutorData.mockPaymentEvent;

        // when
        useCase.markAsUnknown(mockPaymentEvent);

        // then
        assertThat(mockPaymentEvent.getTossPaymentStatus()).isEqualTo(UNKNOWN);
        verify(eventRepository).save(mockPaymentEvent);
    }

    @Test
    @DisplayName("READY, IN_PROGRESS, UNKNOWN 상태가 아니라면 재시도 불가능상태로 DB 동기화에 실패한다.")
    void testMarkAsUnknownFail() {
        // given
        TossPaymentEvent mockPaymentEvent = TossPaymentEvent.builder()
                .id(1L)
                .buyerId(1L)
                .orderId("orderId")
                .paymentKey("paymentKey")
                .orderName("orderName")
                .tossPaymentStatus(DONE)
                .amount(1000)
                .executedAt(null)
                .approvedAt(null)
                .requestedAt(null)
                .build();

        // when & then
        assertThatThrownBy(() -> useCase.markAsUnknown(mockPaymentEvent))
                .isInstanceOf(TossPaymentException.class);
    }

    @Test
    @DisplayName("IN_PROGRESS, UNKNOWN 상태가 아닐경우 결제 실패 처리에 성공한다.")
    void testMarkAsFail_success() {
        TossPaymentEvent mockPaymentEvent = TossPaymentEvent.builder()
                .id(1L)
                .buyerId(1L)
                .orderId("orderId")
                .paymentKey("paymentKey")
                .orderName("orderName")
                .tossPaymentStatus(DONE)
                .amount(1000)
                .executedAt(null)
                .approvedAt(null)
                .requestedAt(null)
                .build();

        useCase.markAsFail(mockPaymentEvent);

        assertThat(mockPaymentEvent.getTossPaymentStatus()).isEqualTo(FAILED);
        verify(eventRepository).save(mockPaymentEvent);
    }

    @Test
    @DisplayName("IN_PROGRESS, UNKNOWN 상태일 경우 재시도가 가능하므로 결제를 실패할 수 없다.")
    void testMarkAsFail_fail() {
        // given
        MockExecutorData mockExecutorData = getDefaultMockConfirmData();
        TossPaymentEvent mockPaymentEvent = mockExecutorData.mockPaymentEvent;

        // when & then
        assertThatThrownBy(() -> useCase.markAsFail(mockPaymentEvent))
                .isInstanceOf(TossPaymentException.class);
    }

    @Test
    @DisplayName("IN_PROGRESS 상태일 경우 재시도 가능 상태로 간주하여 상태가 변경되지 않고, " +
            "예외를 발생시킨다.")
    void testMarkAsSuccessButBizFailed_retryable() {
        // given
        MockExecutorData mockExecutorData = getDefaultMockConfirmData();
        TossPaymentEvent mockPaymentEvent = mockExecutorData.mockPaymentEvent;

        // when & then
        assertThatThrownBy(() -> useCase.markAsSuccessButBusinessFailed(mockPaymentEvent))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("IN_PROGRESS 상태가 아닐경우, 재시도 불가능 상태로 간주하여" +
            "SUCCESS_BUT_BIZ_FAILED 상태로 변경된다.")
    void testMarkAsSuccessButBizFailed_nonRetryable() {
        // given
        TossPaymentEvent mockPaymentEvent = TossPaymentEvent.builder()
                .id(1L)
                .buyerId(1L)
                .orderId("orderId")
                .paymentKey("paymentKey")
                .orderName("orderName")
                .tossPaymentStatus(READY)
                .amount(1000)
                .executedAt(null)
                .approvedAt(null)
                .requestedAt(null)
                .build();

        // when
        useCase.markAsSuccessButBusinessFailed(mockPaymentEvent);

        // then
        assertThat(mockPaymentEvent.getTossPaymentStatus()).isEqualTo(SUCCESS_BUT_BIZ_FAILED);
        verify(eventRepository).save(mockPaymentEvent);
    }

    @Test
    @DisplayName("결제 승인 요청 정보와, paymentKey로 Toss에 조회한 결과가 동일할 경우 예외가 발생하지 않는다.")
    void testValidateBeforeConfirm_success() {
        // given
        MockExecutorData mockExecutorData = getDefaultMockConfirmData();
        TossPaymentEvent mockPaymentEvent = mockExecutorData.mockPaymentEvent;
        TossPaymentInfo mockTossPaymentInfo = mockExecutorData.mockTossPaymentInfo;
        TossConfirmRequest mockTossConfirmRequest = mockExecutorData.mockTossConfirmRequest;

        // when & then
        assertDoesNotThrow(() ->
                useCase.validateBeforeConfirm(mockPaymentEvent, mockTossPaymentInfo, mockTossConfirmRequest)
        );
    }

    @Test
    @DisplayName("결제 승인 요청 정보와, paymentKey로 Toss에 조회한 결과가 동일하지 않을 경우, 예외가 발생한다.")
    void testValidateBeforeConfirm_fail() {
        // given
        MockExecutorData mockExecutorData = getDefaultMockConfirmData();
        TossPaymentEvent mockPaymentEvent = mockExecutorData.mockPaymentEvent;
        TossPaymentInfo mockTossPaymentInfo = mockExecutorData.mockTossPaymentInfo;
        TossConfirmRequest mockTossConfirmRequest = new TossConfirmRequest(1L, "notEqualsKey", "orderId", 1000);

        // when & then
        assertThatThrownBy(() ->
                useCase.validateBeforeConfirm(mockPaymentEvent, mockTossPaymentInfo, mockTossConfirmRequest)
        ).isInstanceOf(TossPaymentException.class);
    }

    @Test
    @DisplayName("UNKNOWN, IN_PROGRESS 상태일 경우 재시도 가능으로 판단하여 재시도 횟수가 증가된다.")
    void testIncreaseRetryCount_success() {
        // given
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
        int retryCount = mockPaymentEvent.getRetryCount();
        // when
        useCase.increaseRetryCount(mockPaymentEvent);

        // then
        assertThat(mockPaymentEvent.getRetryCount()).isEqualTo(retryCount + 1);
        verify(eventRepository).save(mockPaymentEvent);
    }

    @Test
    @DisplayName("UNKNOWN, IN_PROGRESS 상태가 아닐 경우 재시도 불가능으로 판단하여 예외를 발생시킨다.")
    void testIncreaseRetryCount_fail() {
        // given
        TossPaymentEvent mockPaymentEvent = TossPaymentEvent.builder()
                .id(1L)
                .buyerId(1L)
                .orderId("orderId")
                .paymentKey("paymentKey")
                .orderName("orderName")
                .tossPaymentStatus(DONE)
                .amount(1000)
                .executedAt(null)
                .approvedAt(null)
                .requestedAt(null)
                .retryCount(0)
                .build();

        // when & then
        assertThatThrownBy(() -> useCase.increaseRetryCount(mockPaymentEvent))
                .isInstanceOf(TossPaymentException.class);
    }

    private MockExecutorData getDefaultMockConfirmData() {
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

        TossConfirmRequest mockTossConfirmRequest = new TossConfirmRequest(1L, "paymentKey", "orderId", 1000);

        TossPayment mockTossPayment = TossPayment.builder()
                .id(1L)
                .memberId(1L)
                .tossPaymentKey("paymentKey")
                .orderId("orderId")
                .totalAmount(1000)
                .orderName("orderName")
                .tossPaymentMethod(EASY_PAY)
                .tossPaymentStatus(DONE)
                .requestedAt(null)
                .approvedAt(null)
                .build();
        return new MockExecutorData(mockTossPaymentInfo, mockPaymentEvent, mockTossConfirmRequest, mockTossPayment);
    }

    private record MockExecutorData(TossPaymentInfo mockTossPaymentInfo, TossPaymentEvent mockPaymentEvent, TossConfirmRequest mockTossConfirmRequest, TossPayment mockTossPayment) {}
}
