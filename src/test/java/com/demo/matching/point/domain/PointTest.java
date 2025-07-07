package com.demo.matching.point.domain;

import com.demo.matching.core.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PointTest {
    @Test
    @DisplayName("포인트 생성 시 memberId는 그대로, 포인트는 0")
    void create() {
        // given
        Long memberId = 1L;

        // when
        Point result = Point.create(memberId);

        // then
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getPoint()).isZero();
    }

    @Test
    @DisplayName("포인트를 1000만큼 증가시킨다")
    void incrementPoint_once() {
        // given
        Point point = Point.create(1L);

        // when
        Point result = point.incrementPoint(1000);

        // then
        assertThat(result.getPoint()).isEqualTo(1000);
    }

    @Test
    @DisplayName("포인트를 두 번 증가시키면 누적된다")
    void incrementPoint_multiple() {
        // given
        Point point = Point.create(1L)
                .incrementPoint(500)
                .incrementPoint(300);

        // then
        assertThat(point.getPoint()).isEqualTo(800);
    }

    @Test
    @DisplayName("음수로 포인트 증가 시 예외 발생")
    void incrementPoint_negativeAmount() {
        // given
        Point point = Point.create(1L);

        // then
        assertThatThrownBy(() -> point.incrementPoint(-100))
                .isInstanceOf(BusinessException.class);
    }

}
