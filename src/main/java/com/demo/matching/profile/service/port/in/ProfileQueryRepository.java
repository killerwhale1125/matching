package com.demo.matching.profile.service.port.in;

import com.demo.matching.profile.controller.request.ProfileSearchRequest;
import com.demo.matching.profile.infrastructure.querydsl.dto.MemberProfile;

import java.util.List;

public interface ProfileQueryRepository {
    List<MemberProfile> getProfiles(ProfileSearchRequest request);
}
