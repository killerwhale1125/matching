package com.demo.matching.payment.domain.toss.dto;

import com.demo.matching.member.application.dto.MemberInfoResponse;
import lombok.Builder;

@Builder
public record MemberInfo(Long memberId) {
    public static MemberInfo from(MemberInfoResponse memberInfo) {
        return MemberInfo.builder()
                .memberId(memberInfo.memberId())
                .build();
    }
}
