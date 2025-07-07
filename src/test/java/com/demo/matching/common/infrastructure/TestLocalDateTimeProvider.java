package com.demo.matching.common.infrastructure;

import com.demo.matching.common.mock.MockLocalDateTimeProvider;
import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TestLocalDateTimeProvider {

    private LocalDateTimeProvider localDateTimeProvider = new MockLocalDateTimeProvider(LocalDateTime.now());

    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldReturnCurrentLocalDateTime() {
        // given
        LocalDateTime before = LocalDateTime.now().minusMinutes(3);
        LocalDateTime after = LocalDateTime.now().plusMinutes(3);

        // when
        LocalDateTime actual = localDateTimeProvider.now();

        // then ( actual이 before와 after 사이여야 함 )
        assertThat(actual).isAfterOrEqualTo(before);
        assertThat(actual).isBeforeOrEqualTo(after);
    }
}
