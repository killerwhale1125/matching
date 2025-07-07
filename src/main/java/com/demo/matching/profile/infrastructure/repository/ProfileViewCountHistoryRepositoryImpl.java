package com.demo.matching.profile.infrastructure.repository;

import com.demo.matching.profile.application.port.in.ProfileViewCountHistoryRepository;
import com.demo.matching.profile.domain.ProfileViewCountHistory;
import com.demo.matching.profile.infrastructure.entity.ProfileViewCountHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProfileViewCountHistoryRepositoryImpl implements ProfileViewCountHistoryRepository {
    private final ProfileViewCountHistoryJpaRepository profileViewCountHistoryJpaRepository;


    @Override
    public ProfileViewCountHistory save(ProfileViewCountHistory profileViewCountHistory) {
        return profileViewCountHistoryJpaRepository.save(ProfileViewCountHistoryEntity.from(profileViewCountHistory)).to();
    }

    @Override
    public void saveAll(List<ProfileViewCountHistory> losses) {
        List<ProfileViewCountHistoryEntity> histories = losses.stream().map(ProfileViewCountHistoryEntity::from).toList();
        profileViewCountHistoryJpaRepository.saveAll(histories);
    }
}
