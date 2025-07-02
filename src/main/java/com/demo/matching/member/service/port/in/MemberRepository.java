package com.demo.matching.member.service.port.in;

import com.demo.matching.member.domain.Member;

public interface MemberRepository {

    Member save(Member member);
}
