package com.demo.matching.member.application.dto;

import com.demo.matching.member.domain.Member;
import lombok.Builder;

@Builder
public record MemberInfoResponse(Long memberId) {
    public static MemberInfoResponse from(Member member) {
        return MemberInfoResponse.builder()
                .memberId(member.getId())
                .build();
    }
}
