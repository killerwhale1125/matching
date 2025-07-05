package com.demo.matching.payment.presentation.toss.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TossCheckoutRequest(
        @Min(value = 1, message = "충전 금액은 1보다 커야합니다.")
        long amount, // 포인트 충전 금액
        @NotBlank(message = "주문명은 필수입니다.")
        String orderName,  // 주문 이름
        @NotBlank(message = "회원 아이디는 필수입니다.")
        String memberId  // 회원 아이디
) {}