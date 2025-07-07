package com.demo.matching.profile.scheduler;

import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import com.demo.matching.profile.scheduler.port.ProfileViewCountRecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileViewCountScheduler {

    private final ProfileViewCountRecoveryService profileViewCountRecoveryService;
    private final LocalDateTimeProvider localDateTimeProvider;
    /**
     * 매일 새벽 1시에 실행되는 조회수 동기화 스케줄러
     * 1. 전날의 Redis 캐싱된 프로필 조회수를 모두 조회
     * 2. DB의 viewCount 컬럼에 누적 반영 ( Redis 예외로 발생한 Loss Count 함께 반영 )
     * 3. 동기화 후 Redis 캐시 삭제
     */
    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    public void recoverProfileViewCount() {
        profileViewCountRecoveryService.recoverProfileViewCount(localDateTimeProvider.now().toLocalDate());
    }
}
