package com.demo.matching.point.application;

import com.demo.matching.member.domain.dto.PointInfo;
import com.demo.matching.point.application.port.PointHistoryRepository;
import com.demo.matching.point.application.port.PointRepository;
import com.demo.matching.point.domain.Point;
import com.demo.matching.point.domain.PointHistory;
import com.demo.matching.point.presentation.port.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PointServiceTest {
    private PointService pointService;
    private PointRepository mockPointRepository;
    private PointHistoryRepository mockPointHistoryRepository;

    @BeforeEach
    void setUp() {
        mockPointRepository = mock(PointRepository.class);
        mockPointHistoryRepository = mock(PointHistoryRepository.class);
        pointService = new PointServiceImpl(mockPointRepository, mockPointHistoryRepository);
    }

    @Test
    @DisplayName("회원 포인트를 생성하면 0포인트로 초기화된다")
    void createPoint_success() {
        // given
        Long memberId = 1L;
        Point point = Point.create(memberId);
        when(mockPointRepository.save(any(Point.class))).thenReturn(point);

        // when
        PointInfo result = pointService.createBy(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.point()).isEqualTo(0);
    }

    @Test
    @DisplayName("포인트 충전 시 포인트와 히스토리가 저장된다")
    void chargePoint_success() {
        // given
        Long memberId = 1L;
        String orderId = "ORD-1234";
        long amount = 1000L;
        LocalDateTime approvedAt = LocalDateTime.now();

        Point existingPoint = Point.create(memberId);
        Point incrementedPoint = existingPoint.incrementPoint(amount);

        // 포인트 조회 및 저장
        when(mockPointRepository.findWithLockByMemberId(memberId)).thenReturn(existingPoint);
        when(mockPointRepository.save(any(Point.class))).thenReturn(incrementedPoint);

        // 히스토리 저장
        when(mockPointHistoryRepository.save(any(PointHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        pointService.charge(memberId, orderId, amount, approvedAt);

        // then
        verify(mockPointRepository).findWithLockByMemberId(memberId);
        verify(mockPointRepository).save(any(Point.class));
        verify(mockPointHistoryRepository).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("주문 ID로 포인트 충전 이력이 존재하는지 확인한다")
    void existsByOrderId_true() {
        // given
        String orderId = "ORD-1234";
        when(mockPointHistoryRepository.existsByOrderId(orderId)).thenReturn(true);

        // when
        boolean result = pointService.existsByOrderId(orderId);

        // then
        assertThat(result).isTrue();
        verify(mockPointHistoryRepository).existsByOrderId(orderId);
    }

}
