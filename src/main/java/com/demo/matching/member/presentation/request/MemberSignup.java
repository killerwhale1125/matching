package com.demo.matching.member.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record MemberSignup(@NotBlank(message = "이름은 필수입니다. 빈값이 들어오지 않도록 요청해주세요.") String name) {
}
