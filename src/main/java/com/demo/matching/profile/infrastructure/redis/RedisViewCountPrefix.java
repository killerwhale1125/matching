package com.demo.matching.profile.infrastructure.redis;

import lombok.Getter;

@Getter
public enum RedisViewCountPrefix {
    PROFILE_VIEW("profile:view:"),
    PROFILE_VIEW_HOT("profile:view:hot"),
    PROFILE_VIEW_HOT_SINCE("profile:view:hot:since:");

    private final String key;

    RedisViewCountPrefix(String key) {
        this.key = key;
    }

    public String withSuffix(Object suffix) {
        return key + suffix;
    }
}
