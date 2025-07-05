package com.demo.matching.point.infrastructure.repository;

import com.demo.matching.point.infrastructure.entity.PointEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<PointEntity, Long> {
    /* 서비스 특성 상 결제 빈도가 많지 않기 때문에 비관적 락 사용 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PointEntity p WHERE p.memberId = :memberId")
    Optional<PointEntity> findWithLockByMemberId(@Param("memberId") Long memberId);
}
