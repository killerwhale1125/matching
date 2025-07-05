package com.demo.matching.payment.domain.toss.enums;

/**
 * PG 사와 무관한 결제 수단
 */
public enum PaymentMethod {
    /* 카드 */
    CARD,

    /* 가상 계좌 */
    VIRTUAL_ACCOUNT,

    /* 간편 결제 */
    EASY_PAY,

    /* 모바일 결제 */
    MOBILE,

    /* 계좌 이체 */
    ACCOUNT_TRANSFER,

    /* 문화상품권 */
    GIFT_CERTIFICATE,

    /* 예외 처리용 */
    UNKNOWN
}
