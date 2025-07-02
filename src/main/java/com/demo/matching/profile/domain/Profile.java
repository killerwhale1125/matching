package com.demo.matching.profile.domain;

import com.demo.matching.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Profile {
    private Long id;    // 프로필 pk
    private Member member;  // 사용자 정보
    private int viewCount;  // 조회수
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;

    public static Profile create(Member member) {
        return Profile.builder()
                .member(member)
                .viewCount(0)
                .build();
    }

    public void updateViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
