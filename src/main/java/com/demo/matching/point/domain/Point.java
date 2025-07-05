package com.demo.matching.point.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Point {
    private Long id;
    private Long memberId;
    private long point;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;

    public static Point create(Long memberId) {
        return Point.builder()
                .point(0)
                .memberId(memberId)
                .build();
    }

    public Point incrementPoint(long amount) {
        this.point += amount;
        return this;
    }
}
