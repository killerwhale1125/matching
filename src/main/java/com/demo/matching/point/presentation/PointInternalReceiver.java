package com.demo.matching.point.presentation;

import com.demo.matching.member.domain.dto.PointInfo;
import com.demo.matching.point.presentation.port.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PointInternalReceiver {

    private final PointService pointService;

    public PointInfo createBy(Long memberId) {
        return pointService.createBy(memberId);
    }

    public void chargePoint(Long memberId, String orderId, long amount, LocalDateTime approvedAt) {
        pointService.charge(memberId, orderId, amount, approvedAt);
    }

    public boolean alreadyCharged(String orderId) {
        return pointService.existsByOrderId(orderId);
    }
}
