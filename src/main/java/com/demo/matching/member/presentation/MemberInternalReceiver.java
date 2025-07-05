package com.demo.matching.member.presentation;

import com.demo.matching.member.application.dto.MemberInfoResponse;
import com.demo.matching.member.presentation.port.in.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberInternalReceiver {
    private final MemberService memberService;

    public MemberInfoResponse getMemberInfoById(Long memberId) {
        return memberService.getById(memberId);
    }
}
