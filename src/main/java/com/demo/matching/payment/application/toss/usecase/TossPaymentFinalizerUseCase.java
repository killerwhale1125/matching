package com.demo.matching.payment.application.toss.usecase;

import com.demo.matching.payment.application.usecase.PointChargeUseCase;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.infrastructure.toss.dto.TossPaymentInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TossPaymentFinalizerUseCase {
    private final TossPaymentExecutorUseCase tossPaymentExecutorUseCase;
    private final PointChargeUseCase pointChargeUseCase;

    @Transactional
    public void finalizeSuccess(TossPaymentEvent paymentEvent, TossPaymentInfo response) {
        /* Business 로직 수행 -> 결제 상태 완료 처리 ( 독립 Transaction ) */
        tossPaymentExecutorUseCase.markPaymentAsSuccess(paymentEvent, response);
        /* Business 로직 수행 -> Point 증가 및 Point History 생성 ( 독립 Transaction ) */
        pointChargeUseCase.chargePoint(paymentEvent.getBuyerId(), response.totalAmount(), paymentEvent.getApprovedAt());
    }
}
