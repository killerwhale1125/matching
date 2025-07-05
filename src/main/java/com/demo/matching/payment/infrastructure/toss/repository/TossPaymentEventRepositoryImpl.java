package com.demo.matching.payment.infrastructure.toss.repository;

import com.demo.matching.payment.application.toss.port.in.TossPaymentEventRepository;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.exception.TossPaymentException;
import com.demo.matching.payment.infrastructure.toss.entity.TossPaymentEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.demo.matching.payment.domain.toss.exception.TossPaymentExceptionStatus.PAYMENT_EVENT_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class TossPaymentEventRepositoryImpl implements TossPaymentEventRepository {

    private final TossPaymentEventJpaRepository tossPaymentEventJpaRepository;

    @Override
    public TossPaymentEvent save(TossPaymentEvent tossPaymentEvent) {
        return tossPaymentEventJpaRepository
                .save(TossPaymentEventEntity.from(tossPaymentEvent))
                .to();
    }

    @Override
    public TossPaymentEvent findByOrderId(String orderId) {
        return tossPaymentEventJpaRepository.findByOrderId(orderId)
                .orElseThrow(() -> new TossPaymentException(PAYMENT_EVENT_NOT_FOUND))
                .to();
    }

    @Override
    public List<TossPaymentEvent> findDelayedInProgressOrUnknownEvents(LocalDateTime before) {
        return tossPaymentEventJpaRepository
                .findByInProgressWithTimeConstraintOrUnknown(before)
                .stream()

    }
}
