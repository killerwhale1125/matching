package com.demo.matching.point.application.port;

import com.demo.matching.point.domain.PointHistory;

public interface PointHistoryRepository {
    void save(PointHistory pointHistory);
}
