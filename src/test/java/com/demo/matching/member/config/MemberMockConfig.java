package com.demo.matching.member.config;

import com.demo.matching.member.presentation.port.in.MemberService;
import com.demo.matching.member.mock.MockMemberService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MemberMockConfig {
    @Bean
    public MemberService memberService() {
        return new MockMemberService();
    }
}
