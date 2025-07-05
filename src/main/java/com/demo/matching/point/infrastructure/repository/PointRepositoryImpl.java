package com.demo.matching.point.infrastructure.repository;

import com.demo.matching.core.common.exception.BusinessException;
import com.demo.matching.point.application.port.PointRepository;
import com.demo.matching.point.domain.Point;
import com.demo.matching.point.infrastructure.entity.PointEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.demo.matching.core.common.exception.BusinessResponseStatus.POINT_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Point save(Point point) {
       return pointJpaRepository.save(PointEntity.from(point)).to();
    }

    @Override
    public Point findWithLockByMemberId(Long memberId) {
        return pointJpaRepository.findWithLockByMemberId(memberId).orElseThrow(() -> new BusinessException(POINT_NOT_FOUND)).to();
    }
}
