package com.demo.matching.profile.infrastructure.entity;

import com.demo.matching.profile.domain.ProfileViewCountHistory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "profile_view_count_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileViewCountHistoryEntity {

    @Column(name = "profile_view_count_history")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long profileId;
    private int loss;
    private LocalDate lossDate;

    public static ProfileViewCountHistoryEntity from(ProfileViewCountHistory profileViewCountHistory) {
        ProfileViewCountHistoryEntity entity = new ProfileViewCountHistoryEntity();
        entity.id = profileViewCountHistory.getId();
        entity.profileId = profileViewCountHistory.getProfileId();
        entity.loss = profileViewCountHistory.getLoss();
        entity.lossDate = profileViewCountHistory.getLossDate();
        return entity;
    }

    public ProfileViewCountHistory to() {
        return ProfileViewCountHistory.builder()
                .id(id)
                .profileId(profileId)
                .loss(loss)
                .lossDate(lossDate)
                .build();
    }
}
