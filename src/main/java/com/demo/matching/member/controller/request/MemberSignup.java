package com.demo.matching.member.controller.request;

import jakarta.validation.constraints.NotBlank;

public record MemberSignup(@NotBlank String name) {
}
