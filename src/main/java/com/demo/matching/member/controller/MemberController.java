package com.demo.matching.member.controller;

import com.demo.matching.common.exception.BusinessResponse;
import com.demo.matching.member.controller.port.in.MemberService;
import com.demo.matching.member.controller.request.MemberSignup;
import com.demo.matching.member.controller.response.MemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 가입 API
     */
    @PostMapping("/signup")
    public BusinessResponse<MemberResponse> signup(@Valid @RequestBody MemberSignup request) {
        return new BusinessResponse(memberService.signup(request));
    }
}
