package com.demo.matching.core.common.infrastructure;

import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SystemLocalDateTimeProvider implements LocalDateTimeProvider {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
