package com.demo.matching.profile.infrastructure.scheduler;

import com.demo.matching.profile.infrastructure.repository.ProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Set;

import static com.demo.matching.profile.infrastructure.redis.RedisViewCountPrefix.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileViewCountScheduler {

    private final RedisTemplate<String, Long> longRedisTemplate;
    private final ProfileJpaRepository profileJpaRepository;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    @Scheduled(cron = "0 0 3 * * *")
    public void syncHotProfiles() {
        Set<Long> hotProfileIds = longRedisTemplate.opsForSet().members(PROFILE_VIEW_HOT.getKey());
        if (hotProfileIds == null || hotProfileIds.isEmpty()) return;

        LocalDate today = LocalDate.now();
        for (Long profileId : hotProfileIds) {
            if (profileId == null) {
                log.warn("잘못된 profileId: {}", profileId);
                continue;
            }

            try {
                syncProfileIfNotInteresting(profileId, today);
            } catch (Exception e) {
                log.error("프로필 동기화 중 오류 발생 - profileId: {}", profileId, e);
            }
        }
    }

    private void syncProfileIfNotInteresting(Long profileId, LocalDate today) {
        String viewCountKey = PROFILE_VIEW.withSuffix(profileId);
        String sinceKey = PROFILE_VIEW_HOT_SINCE.withSuffix(profileId);

        Long viewCount = longRedisTemplate.opsForValue().get(viewCountKey);
        if (viewCount == null) return;

        int dbViewCount = profileJpaRepository.getViewCount(profileId);
        int diff = viewCount.intValue() - dbViewCount;
        if (diff <= 0) return;

        Long sinceRaw = longRedisTemplate.opsForValue().get(sinceKey);
        if (sinceRaw == null) return;

        LocalDate sinceDate = LocalDate.parse(String.valueOf(sinceRaw), DATE_FORMAT);
        int daysElapsed = (int) ChronoUnit.DAYS.between(sinceDate, today);

        if (isNotInteresting(daysElapsed, diff)) {
            profileJpaRepository.incrementViewCountBy(profileId, diff);
            longRedisTemplate.opsForSet().remove(PROFILE_VIEW_HOT.getKey(), profileId);
            longRedisTemplate.delete(Arrays.asList(viewCountKey, sinceKey));
        }
    }

    private boolean isNotInteresting(int daysElapsed, int diffCount) {
        return daysElapsed >= 3 || diffCount < 30;
    }
}
