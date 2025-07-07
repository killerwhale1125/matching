package com.demo.matching.payment.application.toss.usecase;

import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import com.demo.matching.payment.application.toss.port.in.TossPaymentEventRepository;
import com.demo.matching.payment.application.toss.port.in.TossPaymentRepository;
import com.demo.matching.payment.domain.toss.TossPayment;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentExecutorUseCase {
    private final LocalDateTimeProvider localDateTimeProvider;
    private final TossPaymentEventRepository tossPaymentEventRepository;
    private final TossPaymentRepository tossPaymentRepository;

    /* 결제 검증 및 정보 저장 */
    public void execute(TossPaymentEvent paymentEvent, TossConfirmRequest request) {
        /* 결제 가능 확인 및 결제 History 상태 변경 */
        paymentEvent.startConfirmAndUpdate(request.paymentKey(), request.amount(), localDateTimeProvider.now());

        /* 변경된 결제 정보를 DB 에 저장 ( 아직 결제 승인 전 ) */
        tossPaymentEventRepository.save(paymentEvent);
    }

    /* 결제 승인 완료 후 결제 상태 변경 */
    @Transactional
    public void markPaymentAsSuccess(TossPaymentEvent paymentEvent, TossPaymentInfo confirmResponse) {
        paymentEvent.success(confirmResponse);
        tossPaymentEventRepository.save(paymentEvent);
        TossPayment tossPayment = TossPayment.create(paymentEvent.getBuyerId(), confirmResponse);
        tossPaymentRepository.save(tossPayment);
    }

    /* Toss 측 승인 완료에도 Point 미충전 시 재시도 로직을 통한 포인트 재충전 */
    public TossPayment recoverFromConfirmedPayment(TossPaymentEvent paymentEvent, TossPaymentInfo paymentInfo) {
        paymentEvent.success(paymentInfo);
        tossPaymentEventRepository.save(paymentEvent);
        TossPayment tossPayment = TossPayment.create(paymentEvent.getBuyerId(), paymentInfo);
        return tossPaymentRepository.save(tossPayment);
    }


    /* 재시도 가능하면 상태 변경 후 DB 동기화 처리 */
    public void markAsUnknown(TossPaymentEvent paymentEvent) {
        paymentEvent.unknown();
        tossPaymentEventRepository.save(paymentEvent);
    }

    /* 결제 실패 처리 */
    public void markAsFail(TossPaymentEvent paymentEvent) {
        paymentEvent.fail();
        tossPaymentEventRepository.save(paymentEvent);
    }

    /* 결제는 성공했지만 비즈니스 로직 실패 상태 */
    public void markAsSuccessButBusinessFailed(TossPaymentEvent paymentEvent) {
        paymentEvent.businessFail();
        tossPaymentEventRepository.save(paymentEvent);
    }

    /* 결제 승인 전 검증 */
    public void validateBeforeConfirm(TossPaymentEvent paymentEvent, TossPaymentInfo findResponse, TossConfirmRequest request) {
        paymentEvent.valid(findResponse, request);
    }

    /* 재시도 횟수 증가 */
    @Transactional
    public void increaseRetryCount(TossPaymentEvent paymentEvent) {
        paymentEvent.increaseRetryCount();
        tossPaymentEventRepository.save(paymentEvent);
    }
}
