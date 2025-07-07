package com.demo.matching.profile.application;

import com.demo.matching.common.mock.MockLocalDateTimeProvider;
import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import com.demo.matching.profile.application.port.in.ProfileRepository;
import com.demo.matching.profile.application.port.in.ProfileViewCountHistoryRepository;
import com.demo.matching.profile.application.port.out.ProfileViewCountPort;
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
        when(profileViewCountPort.getYesterdayKeys(yesterday)).thenReturn(Set.of(redisKey));
        when(profileViewCountPort.getViewCountFromRedis(redisKey)).thenReturn(100);
        when(profileRepository.syncUpdateViewCountBy(profileId, 100)).thenReturn(1);

        // when
        scheduler.recoverProfileViewCount(today);

        // then
        verify(profileViewCountPort).deleteRedisKey(redisKey);
        verify(profileViewCountHistoryRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("조회수 동기화 실패시 히스토리에 날짜가 저장된다.")
    void shouldRecordLossWhenSyncFails() {
        // given
        LocalDate today = mockLocalDateTimeProvider.now().toLocalDate();
        LocalDate yesterday = today.minusDays(1);
        String redisKey = "profile:view:1:" + yesterday;

        when(profileViewCountPort.getYesterdayKeys(yesterday)).thenReturn(Set.of(redisKey));
        when(profileViewCountPort.getViewCountFromRedis(redisKey)).thenReturn(100);
        when(profileRepository.syncUpdateViewCountBy(1L, 100)).thenReturn(0); // 동기화 실패

        // when
        List<ProfileViewCountHistory> result = scheduler.recoverProfileViewCount(today);

        // then

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProfileId()).isEqualTo(1L);
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
        scheduler.recoverProfileViewCount(today);

        // then
        verify(profileRepository, never()).syncUpdateViewCountBy(anyLong(), anyInt());
        verify(profileViewCountHistoryRepository, never()).saveAll(any());
    }
}
