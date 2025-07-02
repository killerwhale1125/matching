package com.demo.matching.profile.controller.response;

import com.demo.matching.profile.domain.Profile;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProfileDetailResponse(int viewCount, LocalDateTime createdTime) {
    public static ProfileDetailResponse from(Profile profile) {
        return ProfileDetailResponse.builder()
                .viewCount(profile.getViewCount())
                .build();
    }
}
