package com.demo.matching.member.mock;

import com.demo.matching.member.controller.port.in.MemberService;
import com.demo.matching.member.controller.response.MemberResponse;
import com.demo.matching.member.domain.Member;
import com.demo.matching.member.controller.request.MemberSignup;
import com.demo.matching.member.service.port.in.MemberRepository;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.mock.MockProfileRepository;
import com.demo.matching.profile.service.port.in.ProfileRepository;

public class MockMemberService implements MemberService {

    private final MemberRepository memberRepository = new MockMemberRepository();
    private final ProfileRepository profileRepository = new MockProfileRepository();

    @Override
    public MemberResponse signup(MemberSignup request) {
        Member member = memberRepository.save(Member.signup(request));
        Profile profile = profileRepository.save(Profile.create(member));

        // 응답 변환
        return MemberResponse.from(member, profile);
    }
}
