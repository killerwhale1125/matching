package com.demo.matching.payment.presentation.toss.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record TossConfirmRequest(
        @NotNull(message = "회원 ID는 필수입니다.")
        Long memberId,

        @NotBlank(message = "paymentKey는 필수입니다.")
        String paymentKey,

        @NotBlank(message = "주문 ID는 필수입니다.")
        String orderId,

        @Min(value = 1, message = "충전 금액은 1보다 커야 합니다.")
        long amount
) {}