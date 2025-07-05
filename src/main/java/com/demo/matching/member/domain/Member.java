package com.demo.matching.member.domain;

import com.demo.matching.member.presentation.request.MemberSignup;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Member {
    private Long id;    // 회원 pk
    private String name;    // 회원 이름
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;

    public static Member signup(MemberSignup memberSignup) {
        return Member.builder()
                .name(memberSignup.name())
                .build();
    }
}
