package com.demo.matching.core.common.infrastructure;

import com.demo.matching.core.common.service.port.UUIDProvider;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SystemUUIDProvider implements UUIDProvider {
    @Override
    public String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
