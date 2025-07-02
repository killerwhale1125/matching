package com.demo.matching.profile.domain;

import com.demo.matching.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileTest {

    @Test
    @DisplayName("create()는 member를 받아 viewCount를 0으로 초기화한다")
    void createProfile() {
        // given
        final Member member = Member.builder().id(1L).name("name").build();

        // when
        final Profile profile = Profile.create(member);

        // then
        assertThat(profile.getMember()).isEqualTo(member);
        assertThat(profile.getViewCount()).isZero();
    }

    @Test
    @DisplayName("updateViewCount() 호출 시 조회수가 정상적으로 변경된다")
    void updateViewCount() {
        // given
        final Member member = Member.builder().id(1L).name("name").build();
        final Profile profile = Profile.create(member);

        // when
        profile.updateViewCount(42);

        // then
        assertThat(profile.getViewCount()).isEqualTo(42);
    }

}
