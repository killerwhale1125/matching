package com.demo.matching.payment.infrastructure.internal;

import com.demo.matching.member.presentation.MemberInternalReceiver;
import com.demo.matching.payment.application.port.out.MemberProvider;
import com.demo.matching.payment.domain.toss.dto.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternalMemberProvider implements MemberProvider {

    private final MemberInternalReceiver memberInternalReceiver;

    @Override
    public MemberInfo getMemberInfoById(Long memberId) {
        return MemberInfo.from(memberInternalReceiver.getMemberInfoById(memberId));
    }
}
