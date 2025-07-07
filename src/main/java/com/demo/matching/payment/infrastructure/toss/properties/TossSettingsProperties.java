package com.demo.matching.payment.infrastructure.toss.properties;

import lombok.Getter;

@Getter
public enum TossSettingsProperties {
    CONNECT_TIMEOUT_SECONDS(1000000),
    READ_TIMEOUT_SECONDS(1000000),
    AUTH_HEADER_PREFIX("Basic "),
    BASIC_DELIMITER(":");

    private int time;
    private String prefix;


    TossSettingsProperties(String prefix) {
        this.prefix = prefix;
    }

    TossSettingsProperties(int time) {
        this.time = time;
    }
}
