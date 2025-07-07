package com.demo.matching.profile.presentation.response;

import com.demo.matching.profile.application.dto.MemberProfile;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberProfileResponse(String name, int viewCount, LocalDateTime registerDate) {
    public static MemberProfileResponse from(MemberProfile memberProfile) {
        return MemberProfileResponse.builder()
                .name(memberProfile.getMemberName())
                .viewCount(memberProfile.getViewCount())
                .registerDate(memberProfile.getCreatedAt())
                .build();
    }
}
