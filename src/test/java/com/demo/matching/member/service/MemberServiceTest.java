package com.demo.matching.member.service;

import com.demo.matching.member.controller.port.in.MemberService;
import com.demo.matching.member.controller.response.MemberResponse;
import com.demo.matching.member.domain.Member;
import com.demo.matching.member.controller.request.MemberSignup;
import com.demo.matching.member.mock.MockMemberRepository;
import com.demo.matching.profile.mock.MockProfileRepository;
import com.demo.matching.profile.domain.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberServiceTest {
    private MemberService memberService;

    /**
     * DB 대용 Mock 어댑터 데이터 추가
     */
    @BeforeEach
    void setUp() {
        final MockMemberRepository mockMemberRepository = new MockMemberRepository();
        final MockProfileRepository mockProfileRepository = new MockProfileRepository();
        this.memberService = new MemberServiceImpl(mockMemberRepository, mockProfileRepository);

        final Member member = Member.builder().id(1L).name("테스트 이름").build();
        mockMemberRepository.save(member);
        final Profile profile = Profile.builder().id(1L).member(member).viewCount(0).build();
        mockProfileRepository.save(profile);
    }

    @Test
    @DisplayName("회원가입 시 회원 정보와 기본 프로필이 함께 생성된다")
    void signup() {
        // given
        final MemberSignup memberSignup = new MemberSignup("이름");

        // when
        final MemberResponse result = memberService.signup(memberSignup);

        // then
        assertThat(result.name()).isEqualTo("이름");
        assertThat(result.profileDetail()).isNotNull();
        assertThat(result.profileDetail().viewCount()).isEqualTo(0);
    }
}
