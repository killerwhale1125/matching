package com.demo.matching.profile.application.port.in;

import com.demo.matching.profile.domain.ProfileViewCountHistory;

import java.util.List;

public interface ProfileViewCountHistoryRepository {
    ProfileViewCountHistory save(ProfileViewCountHistory profileViewCountHistory);

    void saveAll(List<ProfileViewCountHistory> losses);
}
