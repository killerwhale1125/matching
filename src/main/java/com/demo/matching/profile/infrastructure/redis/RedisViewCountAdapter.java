package com.demo.matching.profile.infrastructure.redis;

import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.infrastructure.ProfileJpaRepository;
import com.demo.matching.profile.service.port.out.ProfileViewCountPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.demo.matching.profile.infrastructure.redis.RedisViewCountPrefix.*;

@Component
@RequiredArgsConstructor
public class RedisViewCountAdapter implements ProfileViewCountPort {

    private final RedisTemplate<String, Long> redisTemplate;
    private final ProfileJpaRepository profileJpaRepository;

    @Override
    @Transactional
    public Profile increaseViewCount(Profile profile) {
        Long profileId = profile.getId();
        // Redis에 Hot 후보로 등록되어있는지 확인
        boolean isHot = redisTemplate.opsForSet().isMember(PROFILE_VIEW_HOT.getKey(), profileId);
        if (isHot) {
            // 등록되어있다면 Redis의 조회수를 증가
            int updatedCount = redisTemplate.opsForValue().increment(PROFILE_VIEW.withSuffix(profileId)).intValue();
            profile.updateViewCount(updatedCount);
            return profile;
        }

        profileJpaRepository.incrementViewCount(profileId); // 후보로 등록되어있지 않다면 DB + 1
        int viewCount = (int) profileJpaRepository.getLastUpdatedViewCount();   // 마지막으로 증가시킨 값을 세션에서 조회
        if (viewCount % 10 == 0) {
            // 만약 10회 간격 조회되었다면 Hot 후보 등록
            long today = Long.parseLong(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            redisTemplate.opsForSet().add(PROFILE_VIEW_HOT.getKey(), profileId);
            redisTemplate.opsForValue().set(PROFILE_VIEW.withSuffix(profileId), Long.valueOf(viewCount));
            redisTemplate.opsForValue().set(PROFILE_VIEW_HOT_SINCE.withSuffix(profileId), today);
        }
        profile.updateViewCount(viewCount);
        return profile;
    }
}
