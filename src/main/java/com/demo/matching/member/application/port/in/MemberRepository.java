package com.demo.matching.member.application.port.in;

import com.demo.matching.member.domain.Member;

public interface MemberRepository {

    Member save(Member member);

    Member findById(Long memberId);
}
