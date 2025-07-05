package com.demo.matching.point.application.port;

import com.demo.matching.point.domain.Point;

public interface PointRepository {
    Point save(Point point);

    Point findWithLockByMemberId(Long memberId);
}
