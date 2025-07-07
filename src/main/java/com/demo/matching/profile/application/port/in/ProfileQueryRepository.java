package com.demo.matching.profile.application.port.in;

import com.demo.matching.profile.presentation.request.ProfileSearchRequest;
import com.demo.matching.profile.application.dto.MemberProfile;

import java.util.List;

public interface ProfileQueryRepository {
    List<MemberProfile> getProfiles(ProfileSearchRequest request);
}
