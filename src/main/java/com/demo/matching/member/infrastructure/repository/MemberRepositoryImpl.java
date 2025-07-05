package com.demo.matching.member.infrastructure.repository;

import com.demo.matching.core.common.exception.BusinessException;
import com.demo.matching.member.domain.Member;
import com.demo.matching.member.infrastructure.entity.MemberEntity;
import com.demo.matching.member.application.port.in.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.demo.matching.core.common.exception.BusinessResponseStatus.MEMBER_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        MemberEntity entity = MemberEntity.from(member);
        return memberJpaRepository.save(entity).to();
    }

    @Override
    public Member findById(Long memberId) {
        return memberJpaRepository.findById(memberId).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND)).to();
    }
}
