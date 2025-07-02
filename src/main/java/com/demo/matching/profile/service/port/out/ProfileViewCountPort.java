package com.demo.matching.profile.service.port.out;

import com.demo.matching.profile.domain.Profile;

public interface ProfileViewCountPort {
    Profile increaseViewCount(Profile profile);
}
