package com.demo.matching.profile.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProfileViewCountHistory {
    private Long id;
    private Long profileId;
    private int loss;
    private LocalDate lossDate;

    public static ProfileViewCountHistory create(Long profileId, LocalDate lossDate) {
        return ProfileViewCountHistory.builder()
                .profileId(profileId)
                .loss(0)
                .lossDate(lossDate)
                .build();
    }
}
