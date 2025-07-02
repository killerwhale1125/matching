package com.demo.matching.member.controller;

import com.demo.matching.member.controller.port.in.MemberService;
import com.demo.matching.member.controller.response.MemberResponse;
import com.demo.matching.member.controller.request.MemberSignup;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signup(@Valid @RequestBody MemberSignup request) {
        return ResponseEntity.ok(memberService.signup(request));
    }
}
