package com.demo.matching.profile.infrastructure.redis;

import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.infrastructure.ProfileJpaRepository;
import com.demo.matching.profile.infrastructure.querydsl.dto.MemberProfile;
import com.demo.matching.profile.service.port.out.ProfileViewCountPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.demo.matching.profile.infrastructure.redis.RedisViewCountPrefix.*;

@Component
@RequiredArgsConstructor
public class RedisViewCountAdapter implements ProfileViewCountPort {
    /* RedisTemplate for Long: 직렬화 설정은 별도 Config에서 처리 */
    private final RedisTemplate<String, Long> longRedisTemplate;
    private final ProfileJpaRepository profileJpaRepository;

    /**
     * 프로필 조회 시 조회수를 증가시키고, Hot 후보 조건을 만족하면 Redis에 캐싱
     * - Redis 에 등록된 Hot 프로필일 경우: Redis 에서 조회수 증가
     * - 그렇지 않으면 : DB 조회수 증가 후 일정 조건(10회 단위)에서 Redis에 캐싱
     */
    @Override
    @Transactional
    public Profile increaseViewCount(Profile profile) {
        Long profileId = profile.getId();
        /* Redis Hot 후보 여부 확인 */
        boolean isHot = longRedisTemplate.opsForSet().isMember(PROFILE_VIEW_HOT.getKey(), profileId);
        if (isHot) {
            /* Redis 조회수 + 1 증가 */
            int updatedCount = longRedisTemplate.opsForValue().increment(PROFILE_VIEW.withSuffix(profileId)).intValue();
            profile.updateViewCount(updatedCount);
            return profile;
        }

//        해당 쿼리는 Deprecated 이며, MySQL 8.0.20 이전 버전일 경우 사용
//        profileJpaRepository.incrementViewCountAndReturn(profileId);
//        int viewCount = (int) profileJpaRepository.getLastUpdatedViewCount();
        
        /* Hot 후보가 아니라면 DB 조회수 증가 */
        int viewCount = profileJpaRepository.incrementViewCountAndReturn(profileId);

        /* 조회수가 10의 배수인 경우 Hot 후보로 등록하고 Redis에 캐싱 */
        if (viewCount % 10 == 0) {
            long today = Long.parseLong(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));

            /* Hot 후보 Set profile:view:hot -> { 1(인기 프로필 후보), 2, 3, 4... } */
            longRedisTemplate.opsForSet().add(PROFILE_VIEW_HOT.getKey(), profileId);

            /* 실제 캐싱될 조회수 profile:view:${profileId} -> 35(조회수) */
            longRedisTemplate.opsForValue().set(PROFILE_VIEW.withSuffix(profileId), Long.valueOf(viewCount));

            /* 추후 Hot 후보 탈락을 위한 profile:view:hot:since:${profileId} -> 20250701(캐싱된 날짜) */
            longRedisTemplate.opsForValue().set(PROFILE_VIEW_HOT_SINCE.withSuffix(profileId), today);
        }
        profile.updateViewCount(viewCount);
        return profile;
    }

    /**
     * 캐싱된 조회수를 Redis 에서 가져오기 위한 작업
     */
    @Override
    public Map<Long, Integer> getViewCountsBy(List<MemberProfile> memberProfiles) {
        /* profileId 를 Redis 키값 형식으로 변환 */
        List<String> profileIdRedisKeys = memberProfiles.stream()
                .map(p -> PROFILE_VIEW.withSuffix(p.getProfileId()))
                .toList();
        /*
         - 현재 캐싱 되어 있는 조회수를 Bulk 조회하여 네트워크 비용 및 DB I/O 횟수 단축
         - 캐싱되지 않은 key 는 null 로 처리되어 기존 파라미터로 받은 memberProfiles 와 순서가 유지된다.
        */
        List<Long> cacheViewCounts = longRedisTemplate.opsForValue().multiGet(profileIdRedisKeys);

        /* 캐싱된 조회수 값을  Map<profileId, viewCount> 형태로 저장하여 return */
        Map<Long, Integer> cacheMap = new HashMap<>();
        for (int i = 0; i < memberProfiles.size(); i++) {
            MemberProfile profile = memberProfiles.get(i);
            Long cacheViewCount = cacheViewCounts.get(i);

            if (cacheViewCount != null) {
                cacheMap.put(profile.getProfileId(), cacheViewCount.intValue());
            }
        }

        return cacheMap;
    }
}
