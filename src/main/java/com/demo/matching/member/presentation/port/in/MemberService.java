package com.demo.matching.member.presentation.port.in;

import com.demo.matching.member.application.dto.MemberInfoResponse;
import com.demo.matching.member.presentation.request.MemberSignup;
import com.demo.matching.member.presentation.response.MemberResponse;
import jakarta.validation.Valid;

public interface MemberService {
    MemberResponse signup(@Valid MemberSignup request);
    MemberInfoResponse getById(Long memberId);
}
