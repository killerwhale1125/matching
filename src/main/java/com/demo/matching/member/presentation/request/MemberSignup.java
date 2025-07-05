package com.demo.matching.member.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record MemberSignup(@NotBlank String name) {
}
