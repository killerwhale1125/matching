package com.demo.matching.profile.mock;

import com.demo.matching.member.domain.Member;
import com.demo.matching.member.controller.request.MemberSignup;
import com.demo.matching.member.mock.MockMemberRepository;
import com.demo.matching.member.service.port.in.MemberRepository;
import com.demo.matching.profile.controller.port.in.ProfileService;
import com.demo.matching.profile.controller.request.ProfileSearchRequest;
import com.demo.matching.profile.controller.response.ProfileDetailResponse;
import com.demo.matching.profile.controller.response.ProfileListResponse;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.service.port.in.ProfileRepository;
import com.demo.matching.profile.service.port.out.ProfileViewCountPort;

public class MockProfileService implements ProfileService {

    private final ProfileRepository profileRepository = new MockProfileRepository();
    private final ProfileViewCountPort profileViewCountPort = new MockRedisViewCountAdapter();

    @Override
    public ProfileDetailResponse getProfileDetail(Long profileId) {
        profileRepository.save(Profile.create(initMember()));
        Profile profile = profileRepository.findById(profileId);
        return ProfileDetailResponse.from(profileViewCountPort.increaseViewCount(profile));
    }

    @Override
    public ProfileListResponse getProfiles(ProfileSearchRequest request) {
        return null;
    }

    private Member initMember() {
        MemberRepository memberRepository = new MockMemberRepository();
        return memberRepository.save(Member.signup(new MemberSignup("테스트이름")));
    }
}
