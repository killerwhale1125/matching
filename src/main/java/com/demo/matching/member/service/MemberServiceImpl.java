package com.demo.matching.member.service;

import com.demo.matching.member.controller.port.in.MemberService;
import com.demo.matching.member.controller.response.MemberResponse;
import com.demo.matching.member.domain.Member;
import com.demo.matching.member.controller.request.MemberSignup;
import com.demo.matching.member.service.port.in.MemberRepository;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.service.port.in.ProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public MemberResponse signup(MemberSignup memberSignup) {
        Member member = memberRepository.save(Member.signup(memberSignup));
        Profile profile = profileRepository.save(Profile.create(member));
        return MemberResponse.from(member, profile);
    }
}
