package com.demo.matching.profile.application.port.in;

import com.demo.matching.profile.domain.Profile;

public interface ProfileRepository {
    Profile save(Profile profile);

    Profile findById(Long profileId);
}
