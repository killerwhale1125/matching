package com.demo.matching.point.domain;

import com.demo.matching.point.domain.enums.PointHistoryType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.demo.matching.point.domain.enums.PointHistoryType.*;

@Getter
@Builder
public class PointHistory {
    private Long id;
    private Long memberId;
    private String orderId;
    private long amount;
    private PointHistoryType type;
    private LocalDateTime approvedAt;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;

    public static PointHistory create(Long memberId, String orderId, long amount, LocalDateTime approvedAt) {
        return PointHistory.builder()
                .memberId(memberId)
                .orderId(orderId)
                .amount(amount)
                .type(CHARGE)
                .approvedAt(approvedAt)
                .build();
    }
}
