package com.demo.matching.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PointHistoryTest {
    
    @Test
    @DisplayName("포인트 이력 생성")
    void create() {
        // given
        final Long memberId = 1L;
        final String orderId = "orderId";
        final long amount = 1000;
        final LocalDateTime approvedAt = LocalDateTime.now();

        // when
        final PointHistory result = PointHistory.create(memberId, orderId, amount, approvedAt);

        // then
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getAmount()).isEqualTo(amount);
        assertThat(result.getApprovedAt()).isEqualTo(approvedAt);
    }

}
