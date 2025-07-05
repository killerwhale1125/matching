package com.demo.matching.member.application.usecase;

import com.demo.matching.member.domain.dto.PointInfo;
import com.demo.matching.payment.application.port.out.PointProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointPersistenceUseCase {

    private final PointProvider pointProvider;

    public PointInfo createBy(Long memberId) {
        return pointProvider.createBy(memberId);
    }
}
