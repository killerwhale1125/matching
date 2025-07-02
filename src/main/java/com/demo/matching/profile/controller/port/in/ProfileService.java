package com.demo.matching.profile.controller.port.in;

import com.demo.matching.profile.controller.request.ProfileSearchRequest;
import com.demo.matching.profile.controller.response.ProfileDetailResponse;
import com.demo.matching.profile.controller.response.ProfileListResponse;

public interface ProfileService {

    ProfileDetailResponse getProfileDetail(Long profileId);

    ProfileListResponse getProfiles(ProfileSearchRequest request);
}
