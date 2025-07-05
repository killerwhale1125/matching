package com.demo.matching.member.presentation.response;

import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.PointInfo;
import com.demo.matching.member.domain.dto.ProfileInfo;
import lombok.Builder;

@Builder
public record MemberResponse(String name, ProfileInfo profileInfo, PointInfo pointInfo) {
    public static MemberResponse from(Member member, ProfileInfo profile, PointInfo pointInfo) {
        return MemberResponse.builder()
                .name(member.getName())
                .profileInfo(profile)
                .pointInfo(pointInfo)
                .build();
    }
}
