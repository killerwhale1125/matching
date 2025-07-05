package com.demo.matching.point.application;

import com.demo.matching.member.domain.dto.PointInfo;
import com.demo.matching.point.application.port.PointHistoryRepository;
import com.demo.matching.point.application.port.PointRepository;
import com.demo.matching.point.domain.Point;
import com.demo.matching.point.domain.PointHistory;
import com.demo.matching.point.presentation.port.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /* 반드시 결제 승인이 완료된 후에만 충전 가능 */
    @Override
    public void charge(Long memberId, long amount, LocalDateTime approvedAt) {
        // 회원의 포인트 정보 조회 ( 비관적 락 )
        Point point = pointRepository.findWithLockByMemberId(memberId);
        point = point.incrementPoint(amount);
        pointRepository.save(point);

        // 포인트 충전 이력 저장
        PointHistory pointHistory = PointHistory.create(memberId, amount, approvedAt);
        pointHistoryRepository.save(pointHistory);
    }

    @Override
    public PointInfo createBy(Long memberId) {
        return PointInfo.from(pointRepository.save(Point.create(memberId)).getPoint());
    }
}
