package com.demo.matching.profile.presentation.port.in;

import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.ProfileInfo;
import com.demo.matching.profile.presentation.request.ProfileSearchRequest;
import com.demo.matching.profile.presentation.response.MemberProfileResponse;
import com.demo.matching.profile.presentation.response.ProfileDetailResponse;

import java.util.List;

public interface ProfileService {

    ProfileDetailResponse getProfileDetail(Long profileId);

    List<MemberProfileResponse> getProfiles(ProfileSearchRequest request);

    ProfileInfo create(Member member);
}
