package com.demo.matching.profile.infrastructure.redis;

import lombok.Getter;

@Getter
public enum RedisViewCountPrefix {
    /* 조회수 ex ) profile:view:${profileId} -> 35(조회수) */
    PROFILE_VIEW("profile:view:"),

    /* Hot 유저로 선정된 Set 정보 ex ) profile:view:hot -> { 1, 2, 3, 4... } */
    PROFILE_VIEW_HOT("profile:view:hot"),

    /* 해당 프로필의 조회수가 캐싱된 날짜 ex ) profile:view:hot:since:${profileId} -> 20250701 */
    PROFILE_VIEW_HOT_SINCE("profile:view:hot:since:");

    private final String key;

    RedisViewCountPrefix(String key) {
        this.key = key;
    }

    /* 조회수 전용 Redis value 특성상 Number 자식 객체만 받도록 설정 */
    public <T extends Number> String withSuffix(T suffix) {
        return key + suffix;
    }
}
