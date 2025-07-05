package com.demo.matching.member.application;

import com.demo.matching.member.application.dto.MemberInfoResponse;
import com.demo.matching.member.application.port.in.MemberRepository;
import com.demo.matching.member.application.usecase.PointPersistenceUseCase;
import com.demo.matching.member.application.usecase.ProfilePersistenceUseCase;
import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.PointInfo;
import com.demo.matching.member.domain.dto.ProfileInfo;
import com.demo.matching.member.presentation.port.in.MemberService;
import com.demo.matching.member.presentation.request.MemberSignup;
import com.demo.matching.member.presentation.response.MemberResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final ProfilePersistenceUseCase profilePersistenceUsecase;
    private final PointPersistenceUseCase pointPersistenceUseCase;

    @Override
    @Transactional
    public MemberResponse signup(MemberSignup memberSignup) {
        Member member = memberRepository.save(Member.signup(memberSignup));
        return MemberResponse.from(
                member,
                profilePersistenceUsecase.createBy(member),
                pointPersistenceUseCase.createBy(member.getId())
        );
    }

    @Override
    public MemberInfoResponse getById(Long memberId) {
        return MemberInfoResponse.from(memberRepository.findById(memberId));
    }
}
