package com.demo.matching.profile.controller.port.in;

import com.demo.matching.profile.controller.request.ProfileSearchRequest;
import com.demo.matching.profile.controller.response.MemberProfileResponse;
import com.demo.matching.profile.controller.response.ProfileDetailResponse;

import java.util.List;

public interface ProfileService {

    ProfileDetailResponse getProfileDetail(Long profileId);

    List<MemberProfileResponse> getProfiles(ProfileSearchRequest request);
}
