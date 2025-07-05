package com.demo.matching.member.domain.dto;

import lombok.Builder;

@Builder
public record ProfileInfo(int viewCount) {
    public static ProfileInfo from(int viewCount) {
        return ProfileInfo.builder()
                .viewCount(viewCount)
                .build();
    }
}
