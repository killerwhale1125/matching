package com.demo.matching.profile.service;

import com.demo.matching.profile.controller.port.in.ProfileService;
import com.demo.matching.profile.controller.request.ProfileSearchRequest;
import com.demo.matching.profile.controller.response.ProfileDetailResponse;
import com.demo.matching.profile.controller.response.ProfileListResponse;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.service.port.in.ProfileRepository;
import com.demo.matching.profile.service.port.out.ProfileViewCountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileViewCountPort profileViewCountPort;

    @Override
    public ProfileDetailResponse getProfileDetail(Long profileId) {
        Profile profile = profileRepository.findById(profileId);
        return ProfileDetailResponse.from(profileViewCountPort.increaseViewCount(profile));
    }

    @Override
    public ProfileListResponse getProfiles(ProfileSearchRequest request) {
        return null;
    }
}
