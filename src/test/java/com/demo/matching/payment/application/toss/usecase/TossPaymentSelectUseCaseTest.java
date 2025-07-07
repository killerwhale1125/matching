package com.demo.matching.payment.application.toss.usecase;

import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import com.demo.matching.payment.application.toss.port.in.TossPaymentEventRepository;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TossPaymentSelectUseCaseTest {

    private TossPaymentEventRepository repository;
    private LocalDateTimeProvider timeProvider;
    private TossPaymentSelectUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(TossPaymentEventRepository.class);
        timeProvider = mock(LocalDateTimeProvider.class);
        useCase = new TossPaymentSelectUseCase(repository, timeProvider);
    }

    @Test
    @DisplayName("주문 ID로 결제 이벤트 조회")
    void findPaymentEventByOrderId_success() {
        // given
        String orderId = "order-abc";
        TossPaymentEvent mockPaymentEvent = mock(TossPaymentEvent.class);
        when(repository.findByOrderId(orderId)).thenReturn(mockPaymentEvent);

        // when
        TossPaymentEvent result = useCase.findPaymentEventByOrderId(orderId);

        // then
        assertThat(result).isEqualTo(mockPaymentEvent);
        verify(repository).findByOrderId(orderId);
    }

    @Test
    @DisplayName("재시도 가능한 결제 이벤트 목록 조회")
    void getRetryablePaymentEvents_success() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minusTime = now.minusMinutes(TossPaymentEvent.RETRYABLE_MINUTES_FOR_IN_PROGRESS);
        List<TossPaymentEvent> events = List.of(mock(TossPaymentEvent.class));

        when(timeProvider.now()).thenReturn(now);
        when(repository.findDelayedInProgressOrUnknownEvents(minusTime)).thenReturn(events);

        // when
        List<TossPaymentEvent> result = useCase.getRetryablePaymentEvents();

        // then
        assertThat(result).hasSize(1);
        verify(timeProvider).now();
        verify(repository).findDelayedInProgressOrUnknownEvents(minusTime);
    }
}
