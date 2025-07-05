package com.demo.matching.payment.infrastructure.toss;

import lombok.Getter;

@Getter
public enum TossSettingsPrefix {
    CONNECT_TIMEOUT_SECONDS(1000000),
    READ_TIMEOUT_SECONDS(1000000),
    AUTH_HEADER_PREFIX("Basic "),
    BASIC_DELIMITER(":");

    private int time;
    private String prefix;


    TossSettingsPrefix(String prefix) {
        this.prefix = prefix;
    }

    TossSettingsPrefix(int time) {
        this.time = time;
    }
}
