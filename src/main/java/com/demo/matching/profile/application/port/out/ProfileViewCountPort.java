package com.demo.matching.profile.application.port.out;

import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.infrastructure.querydsl.dto.MemberProfile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProfileViewCountPort {
    Profile increaseViewCount(Profile profile);

    Map<Long, Integer> getViewCountsBy(List<MemberProfile> memberProfiles);

    Integer getViewCountFromRedis(String key);

    void deleteRedisKey(String key);

    Set<String> getYesterdayKeys(LocalDate date);
}
