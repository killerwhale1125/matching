package com.demo.matching.payment.application.toss.usecase;

import com.demo.matching.core.common.service.port.LocalDateTimeProvider;
import com.demo.matching.payment.application.toss.port.in.TossPaymentEventRepository;
import com.demo.matching.payment.application.toss.port.in.TossPaymentRepository;
import com.demo.matching.payment.domain.toss.TossPayment;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.infrastructure.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import com.demo.matching.payment.presentation.toss.response.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TossPaymentExecutorUseCase {
    private final LocalDateTimeProvider localDateTimeProvider;
    private final TossPaymentEventRepository tossPaymentEventRepository;
    private final TossPaymentRepository tossPaymentRepository;

    public void execute(TossPaymentEvent paymentEvent, TossConfirmRequest request) {

        /* 결제 가능 확인 및 결제 History 상태 변경 */
        paymentEvent.startConfirmAndUpdate(request.paymentKey(), request.amount(), localDateTimeProvider.now());

        /* 변경된 결제 정보를 DB 에 저장 ( 아직 결제 승인 전 ) */
        tossPaymentEventRepository.save(paymentEvent);
    }

    /* 결제 승인 완료 후 결제 상태 변경 */
    public void markPaymentAsSuccess(TossPaymentEvent paymentEvent, TossConfirmResponse response) {
        paymentEvent.success(response.approvedAt());
        tossPaymentEventRepository.save(paymentEvent);
        TossPayment tossPayment = TossPayment.create(paymentEvent.getBuyerId(), paymentEvent.getOrderName(), response);
        tossPaymentRepository.save(tossPayment);
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

    public void validateBeforeConfirm(TossPaymentEvent paymentEvent, TossPaymentInfo findResponse, TossConfirmRequest request) {
        paymentEvent.valid(findResponse, request);
    }
}
