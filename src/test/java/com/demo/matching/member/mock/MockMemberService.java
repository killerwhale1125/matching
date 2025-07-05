package com.demo.matching.member.mock;

import com.demo.matching.member.application.dto.MemberInfoResponse;
import com.demo.matching.member.presentation.port.in.MemberService;
import com.demo.matching.member.presentation.response.MemberResponse;
import com.demo.matching.member.domain.Member;
import com.demo.matching.member.presentation.request.MemberSignup;
import com.demo.matching.member.application.port.in.MemberRepository;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.mock.MockProfileRepository;
import com.demo.matching.profile.application.port.in.ProfileRepository;

public class MockMemberService implements MemberService {

    private final MemberRepository memberRepository = new MockMemberRepository();
    private final ProfileRepository profileRepository = new MockProfileRepository();

    @Override
    public MemberResponse signup(MemberSignup request) {
        /* Member 생성 */
        final Member member = memberRepository.save(Member.signup(request));
        /* Profile 생성 */
        final Profile profile = profileRepository.save(Profile.create(member));
        return MemberResponse.from(member, profile);
    }

    @Override
    public MemberInfoResponse getById(Long memberId) {
        return null;
    }
}
