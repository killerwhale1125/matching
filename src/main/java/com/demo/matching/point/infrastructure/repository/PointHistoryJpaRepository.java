package com.demo.matching.point.infrastructure.repository;

import com.demo.matching.point.infrastructure.entity.PointHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistoryEntity, Long> {
    boolean existsByOrderId(String orderId);
}
