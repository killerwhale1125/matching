package com.demo.matching.profile.application;

import com.demo.matching.common.mock.MockLocalDateTimeProvider;
import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import com.demo.matching.profile.application.port.in.ProfileRepository;
import com.demo.matching.profile.application.port.in.ProfileViewCountHistoryRepository;
import com.demo.matching.profile.application.port.out.ProfileViewCountPort;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.domain.ProfileViewCountHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ProfileViewCountRecoverySchedulerTest {
    private ProfileViewCountPort profileViewCountPort;
    private ProfileRepository profileRepository;
    private ProfileViewCountHistoryRepository profileViewCountHistoryRepository;

    private ProfileViewCountRecoveryScheduler scheduler;
    private LocalDateTimeProvider mockLocalDateTimeProvider = new MockLocalDateTimeProvider(LocalDateTime.now());


    @BeforeEach
    void setUp() {
        profileViewCountPort = mock(ProfileViewCountPort.class);
        profileRepository = mock(ProfileRepository.class);
        profileViewCountHistoryRepository = mock(ProfileViewCountHistoryRepository.class);

        scheduler = new ProfileViewCountRecoveryScheduler(
                profileViewCountPort,
                profileRepository,
                profileViewCountHistoryRepository
        );
    }

    @Test
    @DisplayName("조회수가 정상적으로 DB와 동기화된다.")
    void shouldSyncViewCountFromRedis() {
        // given
        LocalDate today = mockLocalDateTimeProvider.now().toLocalDate();
        LocalDate yesterday = today.minusDays(1);
        String redisKey = "profile:view:1:" + yesterday;
        Long profileId = 1L;
        Integer viewCount = 100;

        Profile mockProfile = mock(Profile.class);

        when(profileViewCountPort.getYesterdayKeys(yesterday)).thenReturn(Set.of(redisKey));
        when(profileViewCountPort.getViewCountFromRedis(redisKey)).thenReturn(viewCount);
        when(profileRepository.findById(profileId)).thenReturn(mockProfile);

        // when
        List<ProfileViewCountHistory> result = scheduler.recoverProfileViewCount(today);

        // then
        verify(mockProfile).syncViewCount(viewCount);
        verify(profileRepository).save(mockProfile);
        verify(profileViewCountPort).deleteRedisKey(redisKey);
        verify(profileViewCountHistoryRepository, never()).saveAll(any());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("조회수 동기화 실패시 히스토리에 날짜가 저장된다.")
    void shouldRecordLossWhenSyncFails() {
        // given
        LocalDate today = mockLocalDateTimeProvider.now().toLocalDate();
        LocalDate yesterday = today.minusDays(1);
        String redisKey = "profile:view:1:" + yesterday;
        Long profileId = 1L;

        when(profileViewCountPort.getYesterdayKeys(yesterday)).thenReturn(Set.of(redisKey));
        when(profileViewCountPort.getViewCountFromRedis(redisKey)).thenReturn(100);
        when(profileRepository.findById(profileId)).thenThrow(new RuntimeException("DB 실패"));

        // when
        List<ProfileViewCountHistory> result = scheduler.recoverProfileViewCount(today);

        // then
        verify(profileViewCountHistoryRepository).saveAll(any());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProfileId()).isEqualTo(profileId);
        assertThat(result.get(0).getLossDate()).isEqualTo(yesterday);
    }

    @Test
    @DisplayName("Redis 조회수 null 이면 삭제되고 패스된다.")
    void shouldSkipWhenViewCountIsNullInRedis() {
        // given
        LocalDate today = mockLocalDateTimeProvider.now().toLocalDate();
        LocalDate yesterday = today.minusDays(1);
        String redisKey = "profile:view:1:" + yesterday;

        when(profileViewCountPort.getYesterdayKeys(yesterday)).thenReturn(Set.of(redisKey));
        when(profileViewCountPort.getViewCountFromRedis(redisKey)).thenReturn(null);

        // when
        List<ProfileViewCountHistory> result = scheduler.recoverProfileViewCount(today);

        // then
        verify(profileViewCountPort).deleteRedisKey(redisKey);
        verify(profileViewCountHistoryRepository, never()).saveAll(any());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("조회수 key 리스트가 비어있으면 아무것도 하지 않는다.")
    void shouldReturnEmptyWhenNoRedisKeys() {
        // given
        LocalDate today = mockLocalDateTimeProvider.now().toLocalDate();
        LocalDate yesterday = today.minusDays(1);
        when(profileViewCountPort.getYesterdayKeys(yesterday)).thenReturn(Set.of());

        // when
        List<ProfileViewCountHistory> result = scheduler.recoverProfileViewCount(today);

        // then
        assertThat(result).isEmpty();
        verifyNoInteractions(profileRepository);
        verifyNoInteractions(profileViewCountHistoryRepository);
    }
}
