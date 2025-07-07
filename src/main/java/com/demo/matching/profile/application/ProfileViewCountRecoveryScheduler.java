package com.demo.matching.profile.application;

import com.demo.matching.profile.application.port.in.ProfileRepository;
import com.demo.matching.profile.application.port.in.ProfileViewCountHistoryRepository;
import com.demo.matching.profile.application.port.out.ProfileViewCountPort;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.domain.ProfileViewCountHistory;
import com.demo.matching.profile.scheduler.port.ProfileViewCountRecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileViewCountRecoveryScheduler implements ProfileViewCountRecoveryService {
    private final ProfileViewCountPort profileViewCountPort;
    private final ProfileRepository profileRepository;
    private final ProfileViewCountHistoryRepository profileViewCountHistoryRepository;

    @Override
    public List<ProfileViewCountHistory> recoverProfileViewCount(LocalDate today) {
        LocalDate yesterday = today.minusDays(1);
        Set<String> keys = profileViewCountPort.getYesterdayKeys(yesterday);
        if (keys == null || keys.isEmpty()) return List.of();

        Map<Long, LocalDate> lossMap = new HashMap<>();
        for (String key : keys) {
            try {
                Integer viewCount = profileViewCountPort.getViewCountFromRedis(key);
                if (viewCount == null) {
                    profileViewCountPort.deleteRedisKey(key);  // Redis 메모리 정리
                    continue;
                }

                Long profileId = extractProfileId(key);
                if (profileId == null) continue;

                Profile profile = profileRepository.findById(profileId);
                profile.syncViewCount(viewCount);
                profileRepository.save(profile);

                profileViewCountPort.deleteRedisKey(key);   // Redis 메모리 정리
            } catch (Exception e) {
                handleException(key, yesterday, lossMap);
            }
        }

        if (!lossMap.isEmpty()) {
            return saveAllLossDateToProfileHistory(lossMap);
        }

        return List.of();
    }

    /* loss 조회수 날짜 History 저장 */
    private List<ProfileViewCountHistory> saveAllLossDateToProfileHistory(Map<Long, LocalDate> lossMap) {
        List<ProfileViewCountHistory> losses = lossMap.entrySet().stream()
                .map(entry -> ProfileViewCountHistory.create(entry.getKey(), entry.getValue()))
                .toList();
        profileViewCountHistoryRepository.saveAll(losses);
        return losses;
    }

    /* loss 날짜 기록 */
    private void handleException(String key, LocalDate yesterday, Map<Long, LocalDate> lossMap) {
        Long profileId = extractProfileId(key);
        if (profileId == null) return;

        lossMap.put(profileId, yesterday);
    }

    /**
     * Redis 키에서 프로필 ID 추출
     * ex) "profile:view:123:20250701" → 123
     */
    private Long extractProfileId(String redisKey) {
        try {
            return Long.valueOf(redisKey.split(":")[2]);
        } catch (Exception e) {
            return null;
        }
    }
}
