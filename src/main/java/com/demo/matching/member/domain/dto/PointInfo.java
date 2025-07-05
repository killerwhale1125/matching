package com.demo.matching.member.domain.dto;

import lombok.Builder;

@Builder
public record PointInfo(long point) {
    public static PointInfo from(long point) {
        return PointInfo.builder()
                .point(point)
                .build();
    }
}
