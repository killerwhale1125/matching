package com.demo.matching.payment.application.port.out;

import com.demo.matching.payment.domain.toss.dto.MemberInfo;

public interface MemberProvider {
    MemberInfo getMemberInfoById(Long memberId);
}
