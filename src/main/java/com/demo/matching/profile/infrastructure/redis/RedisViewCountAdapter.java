package com.demo.matching.profile.infrastructure.redis;

import com.demo.matching.core.common.exception.BusinessException;
import com.demo.matching.core.common.exception.BusinessResponseStatus;
import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import com.demo.matching.profile.application.port.out.ProfileViewCountPort;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.application.dto.MemberProfile;
import com.demo.matching.profile.infrastructure.repository.ProfileJpaRepository;
import com.demo.matching.profile.infrastructure.repository.ProfileViewCountHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisViewCountAdapter implements ProfileViewCountPort {
    private final RedisTemplate<String, Integer> integerRedisTemplate;
    private final ProfileViewCountHistoryJpaRepository profileViewCountHistoryJpaRepository;
    private final ProfileJpaRepository profileJpaRepository;
    private final LocalDateTimeProvider localDateTimeProvider;
    private final String REDIS_VIEW_KEY = "profile:view:";

    /**
     * 프로필 상세 조회 시 조회수 증가
     * 1. 오늘 날짜 기반으로 Redis에서 조회수 캐싱 여부 확인
     * 2. cache hit -> Redis 값 +1 (원자적 연산)
     * 3. cache miss → DB 또는 어제자 Redis 값 참조 → Redis에 오늘 날짜로 신규 캐싱
     * 4. Redis 장애 발생 시 viewCountLoss 플래그를 통해 조회수 누락 보정
     */
    @Override
    public Profile increaseViewCount(Profile profile) {
        LocalDate today = localDateTimeProvider.now().toLocalDate();
        String redisKey = REDIS_VIEW_KEY + profile.getId() + ":" + today;

        /* Redis 에 이미 캐싱된 경우 → + 1 수행 후 return */
        if (integerRedisTemplate.hasKey(redisKey)) {
            int updatedCount = integerRedisTemplate.opsForValue().increment(redisKey).intValue();  // 원자적 연산 수행으로 동시성 안전
            return profile.updateViewCount(updatedCount);
        }

        /* Cache Miss → 어제자 Redis or DB 조회 후 Redis 캐싱 시도 */
        try {
            LocalDate yesterday = today.minusDays(1);
            Integer yesterdayViewCount = integerRedisTemplate
                    .opsForValue().get(REDIS_VIEW_KEY + profile.getId() + ":" + yesterday);

            int viewCount = yesterdayViewCount != null
                    ? yesterdayViewCount
                    : profileJpaRepository.getViewCount(profile.getId())
                    .orElseThrow(() -> new BusinessException(BusinessResponseStatus.PROFILE_NOT_FOUND));

            int finalViewCount;

            /* Redis에 값이 없었으므로 새로 캐싱 시도 (동시성 중복 삽입 방지) */
            boolean isInsert = integerRedisTemplate.opsForValue().setIfAbsent(redisKey, viewCount + 1);

            /* 응답용 viewCount (동시성으로 이미 캐싱되었을 경우 반환값 매핑 시 Redis INCR 조회수 증가 후 값 사용 */
            finalViewCount = isInsert
                    ? viewCount + 1
                    : integerRedisTemplate.opsForValue().increment(redisKey).intValue();

            return profile.updateViewCount(finalViewCount);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            /* Redis 장애 발생 시 조회수 누락 보정 플래그 기록 (스케줄링으로 DB 반영) */
            profileViewCountHistoryJpaRepository.markAsLossCount(profile.getId(), 1);
            return profile;
        }
    }

    /**
     * 주어진 프로필 목록에 대해 Redis에서 조회수 캐시값을 일괄 조회
     * - Redis 키 형식: profile:view:{profileId}:{today}
     * - 누락된 값은 null로 반환되며, 순서 일치 보장
     */
    @Override
    public Map<Long, Integer> getViewCountsBy(List<MemberProfile> memberProfiles) {
        LocalDate today = localDateTimeProvider.now().toLocalDate();

        /* Redis 키 생성 */
        List<String> profileIdRedisKeys = memberProfiles.stream()
                .map(p -> REDIS_VIEW_KEY + p.getProfileId() + ":" + today)
                .toList();

        /* Redis 에서 일괄 조회 (null 포함) */
        List<Integer> cacheViewCounts = integerRedisTemplate.opsForValue().multiGet(profileIdRedisKeys);

        /* 캐싱된 조회수 값을  Map<profileId, viewCount> 형태로 저장하여 return */
        Map<Long, Integer> cacheMap = new HashMap<>();
        for (int i = 0; i < memberProfiles.size(); i++) {
            MemberProfile profile = memberProfiles.get(i);
            Integer cacheViewCount = cacheViewCounts.get(i);

            if (cacheViewCount != null) {
                cacheMap.put(profile.getProfileId(), cacheViewCount);
            }
        }

        return cacheMap;
    }

    @Override
    public Integer getViewCountFromRedis(String key) {
        return integerRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteRedisKey(String key) {
        integerRedisTemplate.delete(key);
    }

    @Override
    public Set<String> getYesterdayKeys(LocalDate yesterday) {
        return integerRedisTemplate.keys(REDIS_VIEW_KEY + "*" + ":" + yesterday);
    }
}
