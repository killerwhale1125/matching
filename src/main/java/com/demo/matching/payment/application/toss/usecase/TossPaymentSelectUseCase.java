package com.demo.matching.payment.application.toss.usecase;

import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import com.demo.matching.payment.application.toss.port.in.TossPaymentEventRepository;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TossPaymentSelectUseCase {
    private final TossPaymentEventRepository tossPaymentEventRepository;
    private final LocalDateTimeProvider localDateTimeProvider;

    public TossPaymentEvent findPaymentEventByOrderId(String orderId) {
        return tossPaymentEventRepository.findByOrderId(orderId);
    }

    public List<TossPaymentEvent> getRetryablePaymentEvents() {
        LocalDateTime now = localDateTimeProvider.now().minusMinutes(TossPaymentEvent.RETRYABLE_MINUTES_FOR_IN_PROGRESS);
        return tossPaymentEventRepository.findDelayedInProgressOrUnknownEvents(now);
    }
}
