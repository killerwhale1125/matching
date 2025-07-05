package com.demo.matching.member.application.port.out;

import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.ProfileInfo;

public interface ProfileProvider {
    ProfileInfo createBy(Member member);
}
