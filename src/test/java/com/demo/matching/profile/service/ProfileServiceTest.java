package com.demo.matching.profile.service;

import com.demo.matching.common.exception.BusinessException;
import com.demo.matching.member.domain.Member;
import com.demo.matching.profile.controller.port.in.ProfileService;
import com.demo.matching.profile.controller.response.ProfileDetailResponse;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.mock.MockProfileQueryRepository;
import com.demo.matching.profile.mock.MockProfileRepository;
import com.demo.matching.profile.mock.MockRedisViewCountAdapter;
import com.demo.matching.profile.service.port.in.ProfileQueryRepository;
import com.demo.matching.profile.service.port.in.ProfileRepository;
import com.demo.matching.profile.service.port.out.ProfileViewCountPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProfileServiceTest {
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        final ProfileRepository mockProfileRepository = new MockProfileRepository();
        Member member = Member.builder().id(1L).name("테스트이름").build();
        Profile profile = Profile.builder().id(1L).member(member).viewCount(0).build();
        mockProfileRepository.save(profile);
        final ProfileQueryRepository mockProfileQueryRepository = new MockProfileQueryRepository();
        final ProfileViewCountPort mockRedisViewCountAdapter = new MockRedisViewCountAdapter();
        this.profileService = new ProfileServiceImpl(mockProfileRepository, mockProfileQueryRepository, mockRedisViewCountAdapter);
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void getProfileDetail_Success() {
        // given
        final Long profileId = 1L;

        // when
        final ProfileDetailResponse profileDetail = profileService.getProfileDetail(profileId);

        // then
        assertThat(profileDetail.viewCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("프로필이 저장되어있지 않다면 조회 실패")
    void getProfileDetail_Not_Found_Profile() {
        // given
        final Long profileId = 0L;

        // when then
        assertThatThrownBy(() -> profileService.getProfileDetail(profileId)).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("조회수 증가 시 mock에서도 조회수 값이 증가한다")
    void testMockViewCountIncrease() {
        // given
        final MockRedisViewCountAdapter mockAdapter = new MockRedisViewCountAdapter();
        final Profile profile
                = Profile.builder()
                    .id(1L)
                    .viewCount(0)
                    .member(Member.builder().id(1L).name("테스트").build())
                    .build();
        // when
        final Profile updated = mockAdapter.increaseViewCount(profile);

        // then
        assertThat(updated.getViewCount()).isEqualTo(1);
        assertThat(mockAdapter.getStoredViewCount(1L)).isEqualTo(1);
    }
}
