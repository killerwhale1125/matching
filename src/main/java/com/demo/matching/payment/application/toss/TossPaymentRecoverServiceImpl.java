package com.demo.matching.payment.application.toss;

import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import com.demo.matching.payment.application.toss.port.in.TossPaymentEventRepository;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.scheduler.toss.port.TossPaymentRecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TossPaymentRecoverServiceImpl implements TossPaymentRecoveryService {
    private final TossPaymentEventRepository paymentEventRepository;
    private final LocalDateTimeProvider localDateTimeProvider;

    public List<TossPaymentEvent> getRetryablePaymentEvents() {
        LocalDateTime now = localDateTimeProvider.now().minusMinutes(TossPaymentEvent.RETRYABLE_MINUTES_FOR_IN_PROGRESS);
        return paymentEventRepository.findDelayedInProgressOrUnknownEvents(now);
    }
}
