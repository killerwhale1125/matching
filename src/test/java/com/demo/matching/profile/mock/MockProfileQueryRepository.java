package com.demo.matching.profile.mock;

import com.demo.matching.profile.controller.request.ProfileSearchRequest;
import com.demo.matching.profile.infrastructure.querydsl.dto.MemberProfile;
import com.demo.matching.profile.service.port.in.ProfileQueryRepository;

import java.util.List;

public class MockProfileQueryRepository implements ProfileQueryRepository {
    @Override
    public List<MemberProfile> getProfiles(ProfileSearchRequest request) {
        return List.of();
    }
}
