package com.demo.matching.payment.application.toss.usecase;

import com.demo.matching.payment.application.toss.port.in.TossApiClientPort;
import com.demo.matching.payment.application.usecase.PointChargeUseCase;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.dto.TossPaymentInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TossPaymentFinalizerUseCase {
    private final TossPaymentExecutorUseCase tossPaymentExecutorUseCase;
    private final PointChargeUseCase pointChargeUseCase;
    private final TossApiClientPort tossApiClientPort;

    @Transactional
    public void finalizeSuccess(TossPaymentEvent paymentEvent, TossPaymentInfo response) {
        tossPaymentExecutorUseCase.markPaymentAsSuccess(paymentEvent, response);
        pointChargeUseCase.chargePoint(paymentEvent.getBuyerId(), paymentEvent.getOrderId(), response.totalAmount(), paymentEvent.getApprovedAt());
    }

    @Transactional
    public void markPaymentAsSuccessIfNotYet(TossPaymentEvent paymentEvent) {
        /* 포인트 충전 이력이 있는지 확인 */
        if (pointChargeUseCase.alreadyCharged(paymentEvent.getOrderId())) {
            return;
        }
        TossPaymentInfo paymentInfo = tossApiClientPort.findPaymentByPaymentKey(paymentEvent.getPaymentKey());
        tossPaymentExecutorUseCase.recoverFromConfirmedPayment(paymentEvent, paymentInfo);
        pointChargeUseCase.chargePoint(paymentEvent.getBuyerId(), paymentEvent.getOrderId(), paymentInfo.totalAmount(), paymentEvent.getApprovedAt());
    }
}
