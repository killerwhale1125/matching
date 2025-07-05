package com.demo.matching.payment.scheduler.toss;

import com.demo.matching.payment.scheduler.toss.port.TossPaymentRecoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentScheduler {

    private final TossPaymentRecoveryService tossPaymentRecoveryService;


}
