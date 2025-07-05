package com.demo.matching.member.domain;

import com.demo.matching.member.presentation.request.MemberSignup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberTest {

    @Test
    @DisplayName("signup() 정적 메서드를 통해 name으로 회원 객체를 생성할 수 있다")
    void signup() {
        // given
        final MemberSignup signupDto = new MemberSignup("이름");

        // when
        final Member member = Member.signup(signupDto);

        // then
        assertThat(member.getId()).isNull(); // ID는 null 상태
        assertThat(member.getName()).isEqualTo("이름");
    }
}
