package com.demo.matching.payment.application.usecase;

import com.demo.matching.payment.application.port.out.PointProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointChargeUseCase {
    private final PointProvider pointProvider;

    public void chargePoint(Long memberId, String orderId, long amount, LocalDateTime approvedAt) {
        pointProvider.chargePoint(memberId, orderId, amount, approvedAt);
    }

    public boolean alreadyCharged(String orderId) {
        return pointProvider.alreadyCharged(orderId);
    }
}
