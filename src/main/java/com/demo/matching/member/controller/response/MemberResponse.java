package com.demo.matching.member.controller.response;

import com.demo.matching.member.domain.Member;
import com.demo.matching.profile.controller.response.ProfileDetailResponse;
import com.demo.matching.profile.domain.Profile;
import lombok.Builder;

@Builder
public record MemberResponse(String name, ProfileDetailResponse profileDetail) {
    public static MemberResponse from(Member member, Profile profile) {
        return MemberResponse.builder()
                .name(member.getName())
                .profileDetail(ProfileDetailResponse.from(profile))
                .build();
    }
}
