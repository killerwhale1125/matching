package com.demo.matching.member.application.usecase;

import com.demo.matching.member.domain.dto.PointInfo;
import com.demo.matching.payment.application.port.out.PointProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PointPersistenceUseCaseTest {

    private PointProvider mockPointProvider;
    private PointPersistenceUseCase pointPersistenceUseCase;

    @Test
    void createBy() {
        // given
        Long memberId = 1L;
        PointInfo mockPointInfo = new PointInfo(0);
        when(mockPointProvider.createBy(memberId)).thenReturn(mockPointInfo);

        // when
        PointInfo result = pointPersistenceUseCase.createBy(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.point()).isEqualTo(0);
    }

    @BeforeEach
    void setUp() {
        mockPointProvider = mock(PointProvider.class);
        pointPersistenceUseCase = new PointPersistenceUseCase(mockPointProvider);
    }
}
