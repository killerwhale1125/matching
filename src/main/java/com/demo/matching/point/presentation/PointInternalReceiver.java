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

    public void chargePoint(Long memberId, long amount, LocalDateTime approvedAt) {
        pointService.charge(memberId, amount, approvedAt);
    }

    public PointInfo createBy(Long memberId) {
        return pointService.createBy(memberId);
    }
}
