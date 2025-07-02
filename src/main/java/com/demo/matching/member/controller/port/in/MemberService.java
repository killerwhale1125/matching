package com.demo.matching.member.controller.port.in;

import com.demo.matching.member.controller.response.MemberResponse;
import com.demo.matching.member.controller.request.MemberSignup;
import jakarta.validation.Valid;

public interface MemberService {
    MemberResponse signup(@Valid MemberSignup request);
}
