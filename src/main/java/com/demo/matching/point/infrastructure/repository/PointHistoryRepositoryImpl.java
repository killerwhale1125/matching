package com.demo.matching.point.infrastructure.repository;

import com.demo.matching.point.application.port.PointHistoryRepository;
import com.demo.matching.point.domain.PointHistory;
import com.demo.matching.point.infrastructure.entity.PointHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public void save(PointHistory pointHistory) {
        pointHistoryJpaRepository.save(PointHistoryEntity.from(pointHistory));
    }
}
