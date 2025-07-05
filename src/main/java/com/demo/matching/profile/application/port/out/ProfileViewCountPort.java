package com.demo.matching.profile.application.port.out;

import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.infrastructure.querydsl.dto.MemberProfile;

import java.util.List;
import java.util.Map;

public interface ProfileViewCountPort {
    Profile increaseViewCount(Profile profile);

    Map<Long, Integer> getViewCountsBy(List<MemberProfile> memberProfiles);
}
