package com.demo.matching.payment.domain.toss.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TossPaymentMethod {
    VIRTUAL_ACCOUNT("가상계좌"),
    EASY_PAY("간편결제"),
    GAME_CULTURE_GIFT("게임문화상품권"),
    ACCOUNT_TRANSFER("계좌이체"),
    BOOK_COUPON("도서문화상품권"),
    CULTURE_GIFT("문화상품권"),
    CARD("카드"),
    MOBILE("휴대폰"),
    NONE("선택되지 않음");

    private final String displayName;

    TossPaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public static TossPaymentMethod fromDisplayName(String value) {
        return Arrays.stream(values())
                .filter(e -> e.displayName.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown method: " + value));
    }
}
