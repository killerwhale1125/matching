package com.demo.matching.profile.mock;

import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.ProfileInfo;
import com.demo.matching.member.presentation.request.MemberSignup;
import com.demo.matching.member.mock.MockMemberRepository;
import com.demo.matching.member.application.port.in.MemberRepository;
import com.demo.matching.profile.presentation.port.in.ProfileService;
import com.demo.matching.profile.presentation.request.ProfileSearchRequest;
import com.demo.matching.profile.presentation.response.ProfileDetailResponse;
import com.demo.matching.profile.presentation.response.MemberProfileResponse;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.application.port.in.ProfileRepository;
import com.demo.matching.profile.application.port.out.ProfileViewCountPort;

import java.util.ArrayList;
import java.util.List;

public class MockProfileService implements ProfileService {

    private final ProfileRepository profileRepository = new MockProfileRepository();
    private final ProfileViewCountPort profileViewCountPort = new MockRedisViewCountAdapter();

    @Override
    public ProfileDetailResponse getProfileDetail(Long profileId) {
        profileRepository.save(Profile.create(initMember()));
        final Profile profile = profileRepository.findById(profileId);
        return ProfileDetailResponse.from(profileViewCountPort.increaseViewCount(profile));
    }

    @Override
    public List<MemberProfileResponse> getProfiles(ProfileSearchRequest request) {
        List<MemberProfileResponse> result = new ArrayList<>();
        Profile profile = profileRepository.save(Profile.create(initMember()));
        MemberProfileResponse memberProfileResponse = MemberProfileResponse.builder()
                .name(profile.getMember().getName())
                .viewCount(profile.getViewCount())
                .registerDate(profile.getCreatedTime())
                .build();
        result.add(memberProfileResponse);
        return result;
    }

    @Override
    public ProfileInfo create(Member member) {
        return null;
    }

    @Override
    public ProfileInfo create() {
        return null;
    }

    private Member initMember() {
        final MemberRepository memberRepository = new MockMemberRepository();
        return memberRepository.save(Member.signup(new MemberSignup("테스트이름")));
    }
}
