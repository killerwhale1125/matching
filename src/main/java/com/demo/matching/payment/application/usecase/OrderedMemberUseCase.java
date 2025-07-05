package com.demo.matching.payment.application.usecase;

import com.demo.matching.payment.application.port.out.MemberProvider;
import com.demo.matching.payment.domain.toss.dto.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderedMemberUseCase {
    private final MemberProvider memberProvider;

    public MemberInfo getMemberInfoById(Long memberId) {
        return memberProvider.getMemberInfoById(memberId);
    }
}
