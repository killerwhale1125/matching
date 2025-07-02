package com.demo.matching.profile.mock;

import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.service.port.out.ProfileViewCountPort;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockRedisViewCountAdapter implements ProfileViewCountPort {
    private final Map<Long, Integer> viewCountStore = new ConcurrentHashMap<>();

    @Override
    public Profile increaseViewCount(Profile profile) {
        int current = viewCountStore.getOrDefault(profile.getId(), profile.getViewCount());
        int updated = current + 1;
        viewCountStore.put(profile.getId(), updated);
        profile.updateViewCount(updated);
        return profile;
    }

    // 테스트 검증용 getter (optional)
    public int getStoredViewCount(Long profileId) {
        return viewCountStore.getOrDefault(profileId, 0);
    }
}
