package com.demo.matching.profile.config;

import com.demo.matching.profile.presentation.port.in.ProfileService;
import com.demo.matching.profile.mock.MockProfileService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ProfileMockConfig {
    @Bean
    public ProfileService profileService() {
        return new MockProfileService();
    }
}

