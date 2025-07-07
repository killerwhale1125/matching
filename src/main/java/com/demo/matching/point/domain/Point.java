package com.demo.matching.point.domain;

import com.demo.matching.core.common.exception.BusinessException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.demo.matching.core.common.exception.BusinessResponseStatus.INVALID_POINT_AMOUNT;

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

    public Point  incrementPoint(long amount) {
        if (amount < 0) {
            throw new BusinessException(INVALID_POINT_AMOUNT);
        }
        this.point += amount;
        return this;
    }
}
