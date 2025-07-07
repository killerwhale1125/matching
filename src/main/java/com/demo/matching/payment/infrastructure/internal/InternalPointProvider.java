package com.demo.matching.payment.infrastructure.internal;

import com.demo.matching.member.domain.dto.PointInfo;
import com.demo.matching.payment.application.port.out.PointProvider;
import com.demo.matching.point.presentation.PointInternalReceiver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InternalPointProvider implements PointProvider {

    private final PointInternalReceiver pointInternalReceiver;

    @Override
    public void chargePoint(Long memberId, String orderId, long amount, LocalDateTime approvedAt) {
        pointInternalReceiver.chargePoint(memberId, orderId, amount, approvedAt);
    }

    @Override
    public PointInfo createBy(Long memberId) {
        return pointInternalReceiver.createBy(memberId);
    }

    @Override
    public boolean alreadyCharged(String orderId) {
        return pointInternalReceiver.alreadyCharged(orderId);
    }
}
