package com.demo.matching.point.application.port;

import com.demo.matching.point.domain.PointHistory;

public interface PointHistoryRepository {
    PointHistory save(PointHistory pointHistory);

    boolean existsByOrderId(String orderId);
}
