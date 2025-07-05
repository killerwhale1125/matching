package com.demo.matching.payment.application.port.out;

import com.demo.matching.member.domain.dto.PointInfo;

import java.time.LocalDateTime;

public interface PointProvider {

    void chargePoint(Long memberId, long amount, LocalDateTime approvedAt);

    PointInfo createBy(Long memberId);
}
