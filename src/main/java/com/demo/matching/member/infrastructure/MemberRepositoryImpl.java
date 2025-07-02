package com.demo.matching.member.infrastructure;

import com.demo.matching.member.domain.Member;
import com.demo.matching.member.infrastructure.entity.MemberEntity;
import com.demo.matching.member.service.port.in.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        MemberEntity entity = MemberEntity.from(member);
        return memberJpaRepository.save(entity).to();
    }
}
