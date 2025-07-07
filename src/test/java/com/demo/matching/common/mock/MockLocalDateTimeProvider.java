package com.demo.matching.common.mock;

import com.demo.matching.core.common.service.port.LocalDateTimeProvider;

import java.time.LocalDateTime;

public class MockLocalDateTimeProvider implements LocalDateTimeProvider {
    private final LocalDateTime fixedDateTime;

    public MockLocalDateTimeProvider(LocalDateTime fixedDateTime) {
        this.fixedDateTime = fixedDateTime;
    }

    @Override
    public LocalDateTime now() {
        return fixedDateTime;
    }
}
