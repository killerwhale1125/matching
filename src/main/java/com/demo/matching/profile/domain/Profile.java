package com.demo.matching.profile.domain;

import com.demo.matching.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Profile {
    /* 프로필 pk */
    private Long id;
    /* 사용자 정보 */
    private Member member;
    /* 조회수 */
    private int viewCount;
    /* 생성 일자 */
    private LocalDateTime createdTime;
    /* 수정 일자 */
    private LocalDateTime modifiedTime;

    /* 회원 정보로 프로필 생성 */
    public static Profile create(Member member) {
        return Profile.builder()
                .member(member)
                .viewCount(0)
                .build();
    }

    /* 조회수 업데이트 */
    public Profile updateViewCount(int viewCount) {
        this.viewCount = viewCount;
        return this;
    }

    public void syncViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
}
