package com.demo.matching.member.domain;

import com.demo.matching.member.controller.request.MemberSignup;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Member {
    private Long id;    // 회원 pk
    private String name;    // 회원 이름

    public static Member signup(MemberSignup memberSignup) {
        return Member.builder()
                .name(memberSignup.name())
                .build();
    }
}
