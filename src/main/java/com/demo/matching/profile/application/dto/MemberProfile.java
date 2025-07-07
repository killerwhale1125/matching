package com.demo.matching.profile.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberProfile {
    private Long profileId;
    private String memberName;
    private int viewCount;
    private LocalDateTime createdAt;

    /* Querydsl 의존 생성자 */
    @QueryProjection
    public MemberProfile(Long profileId, String memberName, int viewCount, LocalDateTime createdAt) {
        this.profileId = profileId;
        this.memberName = memberName;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
    }

    /* 캐시 메모리 조회수 = DB 조회수 동기화 */
    public void syncViewCount(int cacheCount) {
        this.viewCount = cacheCount;
    }
}
