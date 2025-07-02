package com.demo.matching.profile.service;

import com.demo.matching.member.domain.Member;
import com.demo.matching.profile.controller.port.in.ProfileService;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.mock.MockProfileRepository;
import com.demo.matching.profile.mock.MockRedisViewCountAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileServiceTest {
    private ProfileService profileService;
    @BeforeEach
    void setUp() {
        MockProfileRepository mockProfileRepository = new MockProfileRepository();
        MockRedisViewCountAdapter mockRedisViewCountAdapter = new MockRedisViewCountAdapter();
        this.profileService = new ProfileServiceImpl(mockProfileRepository, mockRedisViewCountAdapter);

    }

    @Test
    @DisplayName("조회수 증가 시 mock에서도 조회수 값이 증가한다")
    void testMockViewCountIncrease() {
        // given
        MockRedisViewCountAdapter mockAdapter = new MockRedisViewCountAdapter();
        Profile profile = Profile.builder()
                .id(1L)
                .viewCount(0)
                .member(Member.builder().id(1L).name("테스트").build())
                .build();

        // when
        Profile updated = mockAdapter.increaseViewCount(profile);

        // then
        assertThat(updated.getViewCount()).isEqualTo(1);
        assertThat(mockAdapter.getStoredViewCount(1L)).isEqualTo(1);
    }
}
