package com.demo.matching.profile.application;

import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.ProfileInfo;
import com.demo.matching.profile.application.port.in.ProfileQueryRepository;
import com.demo.matching.profile.application.port.in.ProfileRepository;
import com.demo.matching.profile.application.port.out.ProfileViewCountPort;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.application.dto.MemberProfile;
import com.demo.matching.profile.presentation.request.ProfileSearchRequest;
import com.demo.matching.profile.presentation.response.MemberProfileResponse;
import com.demo.matching.profile.presentation.response.ProfileDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.demo.matching.profile.domain.enums.ProfileSortType.LATEST;
import static com.demo.matching.profile.domain.enums.ProfileSortType.VIEWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ProfileServiceTest {
    private ProfileServiceImpl profileService;
    private ProfileRepository mockProfileRepository;
    private ProfileQueryRepository mockProfileQueryRepository;
    private ProfileViewCountPort mockProfileViewCountPort;
    private final LocalDateTime FIXED_DATE_TIME = LocalDateTime.now();

    @Test
    @DisplayName("프로필 상세 조회 성공")
    void getProfileDetail_success() {
        // given
        Long profileId = 1L;
        Member member = Member.builder().id(1L).name("이름").build();
        Profile profile = Profile.builder().id(profileId).viewCount(0).member(member).build();
        Profile profileAfterView = Profile.builder().id(profileId).viewCount(1).member(member).build();

        when(mockProfileRepository.findById(profileId)).thenReturn(profile);
        when(mockProfileViewCountPort.increaseViewCount(profile)).thenReturn(profileAfterView);

        // when
        ProfileDetailResponse result = profileService.getProfileDetail(profileId);

        // then
        assertThat(result.viewCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("프로필 리스트 조회 - 정렬 없음 (기본 최신순)")
    void getProfiles_defaultSort() {
        // given
        ProfileSearchRequest request = new ProfileSearchRequest(LATEST, 0, 5);
        MemberProfile profile = new MemberProfile(1L, "이름", 0, FIXED_DATE_TIME);
        List<MemberProfile> profileList = List.of(profile);

        when(mockProfileQueryRepository.getProfiles(request)).thenReturn(profileList);
        when(mockProfileViewCountPort.getViewCountsBy(profileList)).thenReturn(Map.of());

        // when
        List<MemberProfileResponse> result = profileService.getProfiles(request);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("이름");
    }

    @Test
    @DisplayName("프로필 리스트 조회 - 조회수 내림차순 정렬")
    void getProfiles_sortedByViewCount() {
        // given
        ProfileSearchRequest request = new ProfileSearchRequest(VIEWS, 0, 5);

        MemberProfile p1 = new MemberProfile(1L, "이름1", 5, FIXED_DATE_TIME);
        MemberProfile p2 = new MemberProfile(2L, "이름2", 10, FIXED_DATE_TIME);
        List<MemberProfile> profileList = new ArrayList<>(List.of(p1, p2));

        when(mockProfileQueryRepository.getProfiles(request)).thenReturn(profileList);
        when(mockProfileViewCountPort.getViewCountsBy(profileList)).thenReturn(Map.of(
                1L, 5,
                2L, 10
        ));

        // when
        List<MemberProfileResponse> result = profileService.getProfiles(request);

        // then
        assertThat(result.get(0).viewCount()).isEqualTo(10); // 조회수 높은 순
        assertThat(result.get(1).viewCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("프로필 생성 시 초기 viewCount 는 0")
    void createProfile_success() {
        // given
        Member member = Member.builder().id(10L).name("이름").build();
        Profile savedProfile = Profile.builder().id(5L).viewCount(0).member(member).build();

        when(mockProfileRepository.save(any())).thenReturn(savedProfile);

        // when
        ProfileInfo result = profileService.create(member);

        // then
        assertThat(result.viewCount()).isEqualTo(0);
    }

    @BeforeEach
    void setUp() {
        mockProfileRepository = mock(ProfileRepository.class);
        mockProfileQueryRepository = mock(ProfileQueryRepository.class);
        mockProfileViewCountPort = mock(ProfileViewCountPort.class);
        profileService = new ProfileServiceImpl(mockProfileRepository, mockProfileQueryRepository, mockProfileViewCountPort);
    }
}
