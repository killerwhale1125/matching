package com.demo.matching.payment.scheduler.toss;

import com.demo.matching.payment.scheduler.port.TossPaymentRecoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentScheduler {
    private final TossPaymentRecoveryService tossPaymentRecoveryService;
    private static final int FIXED_RATE = 1000 * 60 * 5;    // 5분 주기로 반복

    @Scheduled(fixedRate = FIXED_RATE)
    public void recoverRetryablePayment() {
        tossPaymentRecoveryService.retryPaymentEvents();
    }
}
