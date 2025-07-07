package com.demo.matching.profile.domain;

import com.demo.matching.common.mock.MockLocalDateTimeProvider;
import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileViewCountHistoryTest {
    private final LocalDateTimeProvider localDateTimeProvider = new MockLocalDateTimeProvider(LocalDateTime.now());
    @Test
    @DisplayName("프로필 ID와 날짜를 통해 History 를 생성할 수 있다.")
    void create() {
        // when
        LocalDate lossDate = localDateTimeProvider.now().toLocalDate();

        // when
        ProfileViewCountHistory result = ProfileViewCountHistory.create(1L, lossDate);

        // then
        assertThat(result.getProfileId()).isEqualTo(1L);
        assertThat(result.getLoss()).isEqualTo(0);
        assertThat(result.getLossDate()).isEqualTo(lossDate);
    }
}
