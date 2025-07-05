package com.demo.matching.payment.application.toss.usecase;

import com.demo.matching.payment.application.toss.port.in.TossPaymentEventRepository;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TossPaymentSelectUseCase {
    private final TossPaymentEventRepository tossPaymentEventRepository;

    public TossPaymentEvent findPaymentEventByOrderId(String orderId) {
        return tossPaymentEventRepository.findByOrderId(orderId);
    }
}
