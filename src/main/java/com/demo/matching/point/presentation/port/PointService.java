package com.demo.matching.point.presentation.port;

import com.demo.matching.member.domain.dto.PointInfo;

import java.time.LocalDateTime;

public interface PointService {
    void charge(Long memberId, long amount, LocalDateTime approvedAt);

    PointInfo createBy(Long memberId);
}
