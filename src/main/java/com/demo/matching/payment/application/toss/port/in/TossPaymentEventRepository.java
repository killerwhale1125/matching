package com.demo.matching.payment.application.toss.port.in;

import com.demo.matching.payment.domain.toss.TossPaymentEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface TossPaymentEventRepository {
    TossPaymentEvent save(TossPaymentEvent tossPaymentEvent);

    TossPaymentEvent findByOrderId(String orderId);

    List<TossPaymentEvent> findDelayedInProgressOrUnknownEvents(LocalDateTime before);
}
