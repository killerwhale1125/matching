package com.demo.matching.profile.infrastructure.repository;

import com.demo.matching.core.common.exception.BusinessException;
import com.demo.matching.profile.application.port.in.ProfileRepository;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.infrastructure.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.demo.matching.core.common.exception.BusinessResponseStatus.PROFILE_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfileJpaRepository profileJpaRepository;

    @Override
    public Profile save(Profile profile) {
        return profileJpaRepository.save(ProfileEntity.from(profile)).to();
    }

    @Override
    public Profile findById(Long profileId) {
        return profileJpaRepository.findWithMemberById(profileId)
                .orElseThrow(() -> new BusinessException(PROFILE_NOT_FOUND))
                .to();
    }

    @Override
    public int syncUpdateViewCountBy(Long profileId, Integer viewCount) {
        return profileJpaRepository.syncUpdateViewCountBy(profileId, viewCount);
    }
}
