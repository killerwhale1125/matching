package com.demo.matching.payment.application.toss;

import com.demo.matching.core.common.exception.BusinessException;
import com.demo.matching.payment.application.toss.port.in.TossApiClientPort;
import com.demo.matching.payment.application.toss.usecase.TossPaymentExecutorUseCase;
import com.demo.matching.payment.application.toss.usecase.TossPaymentFinalizerUseCase;
import com.demo.matching.payment.application.toss.usecase.TossPaymentSelectUseCase;
import com.demo.matching.payment.application.usecase.OrderedMemberUseCase;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.exception.TossPaymentConfirmException;
import com.demo.matching.payment.domain.toss.exception.TossPaymentException;
import com.demo.matching.payment.infrastructure.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.presentation.toss.port.in.TossConfirmService;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import com.demo.matching.payment.presentation.toss.response.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.demo.matching.payment.domain.toss.exception.TossPaymentExceptionStatus.NON_RETRYABLE_ERROR;
import static com.demo.matching.payment.domain.toss.exception.TossPaymentExceptionStatus.RETRYABLE_ERROR;

/**
 * 문제점
 * 1. 응답이 지연되어 타임아웃 발생 가능성 ( 서버측은 지연으로 실패처리지만, 토스는 성공? )
 * 2. 실제 결제가 완료되었으나, 서버가 중단되면서 결과를 반환하지 못함 ( 돈은 썼는데 서비스를 못받는다 ? -> 회사 망함 )
 *
 * 해결
 * 1. 재시도 가능 / 불가능 여부 판단
 * 2. 재시도 로직 구현
 * 3. 결제 상태 전환 관리
 * 4. 멱등키 사용 ( 중복 방지 )
 *
 * 재시도가 가능한 에러
 * 1. 일시적 네트워크 문제
 * 2. 외부 서비스 장애
 * 3. API 타임아웃으로 일정 시간 후 재시도로 해결될 수 있는 에러
 * 즉 재시도 처리 스케줄링으로 해결 가능
 * - 재시도 가능한 특정 결제 상태인 결제 요청에 대해 결제 재시도 요청
 * - 재시도가 너무 많아져 시스템에 부하가 걸리는 것을 방지하기 위해 재시도 횟수 제한
 * UNKNOWN : 에러가 발생하였으나, 재시도를 통해 해결 가능성이 있는 상태 ( 재시도 할 수 있는 상태 )
 *
 * 재시도 가능 에러 -> 재시도 로직 수행
 * 재시도 불가능 에러 -> 즉시 결제 실패 처리 및 예외 로직 실행
 *
 * -----------------------------------------------------------------------------------
 * 멱등키 사용 ( 이미 성공한 결제는 재시도 되면 안된다 )
 *  - 각 결제 요청 시 고유한 멱등키를 DB에 저장
 *  - 같은 결제에 대해 다시 요청하면 해당 키로 중복 결제 방지
 *  - 재시도 시 동일 결과 보장 : 이미 토스 측에 성공한 결제더라도, 같은 키를 통해 중복 결제 방지
 *
 *  응답 지연 시 재시도 처리 -> 토스 측 응답 지연으로 결제 실패 시 즉시 실패로 간주하지 않고, 재시도 할 수 있게 처리
 *  중복 결제 방지 -> 이미 토스측에서 성공했는데 결과를 못받았을 경우 멱등키로 중복 결제 방지
 *
 *  결제 승인 요청 중 서버가 중단되는 경우
 *  서버 중단 시 복구 -> IN_PROGRESS 상태인 결제건 재시도
 *  -> 이 때 IN_PROGRESS 더라도 서버에서는 반환받지 못했지만, 토스에선 성공일 수 있으니 멱등키로 중복 재시도 방지
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TossConfirmServiceImpl implements TossConfirmService {
    private final TossPaymentSelectUseCase tossPaymentSelectUseCase;
    private final TossPaymentExecutorUseCase tossPaymentExecutorUseCase;
    private final TossPaymentFinalizerUseCase tossPaymentFinalizerUseCase;
    private final TossApiClientPort tossApiClientPort;
    private final OrderedMemberUseCase orderedMemberUseCase;

    public TossConfirmResponse confirmPayment(TossConfirmRequest request) {
        /* 회원 검증 및 조회 ( 예외 시 바로 Exception Handler 작동 ) */
        orderedMemberUseCase.getMemberInfoById(request.memberId());

        /* orderId 를 통해 주문 정보 조회 ( 예외 시 바로 Exception Handler 작동 ) */
        TossPaymentEvent paymentEvent = tossPaymentSelectUseCase.findPaymentEventByOrderId(request.orderId());

        /* 결제가 가능한 상태인지 확인 -> 결제 정보 생성 ( 예외 시 바로 Exception Handler 작동 ) */
        tossPaymentExecutorUseCase.execute(paymentEvent, request);

        /**
         * 이 시점 TossPaymentEvent 의 Status 값이 UNKNOWN, READY, IN_PROGRESS 상태라면 재시도가 가능
         * 왜 ?
         * Toss 는 승인 요청에 대해 멱등성을 보장하기 때문에 동일한 orderId로 재요청해도 동일한 응답을 반환한다.
         *
         * 위 세가지(UNKNOWN, READY, IN_PROGRESS) 상태는 두 서버측의 알 수 없는 오류로 사용자가 결제에 실패한 억울한 상황이다.
         * ex ) -> 결제 승인 요청하여 Toss 성공
         *      -> 갑작스러운 서버 다운으로 실패
         *      -> UNKNOWN, READY, IN_PROGRESS 상태로 남아있는 결제 요청을 서버가 Toss 에 다시 승인 요청
         *      -> Toss 는 이미 해당 orderId에 대해 승인 성공했다 판단하여 Success 를 응답하고 서버는 다시 포인트 정상적으로 충전 가능 기회 획득
         * 즉 재시도 가능한 상태를 판별하여 재시도 처리하는 스케줄링 및 예외처리가 필수적이다.
         */
        try {

            /* paymentKey 로 Toss 에 GET 요청 정보 조회 ( 존재하면 이미 주문 요청한 유저 ) */
            TossPaymentInfo findResponse = tossApiClientPort.findPaymentByPaymentKey(request.paymentKey());
            tossPaymentExecutorUseCase.validateBeforeConfirm(paymentEvent, findResponse, request);
            /* Toss 에 결제 승인 요청 API 호출 */
            TossConfirmResponse confirmResponse = tossApiClientPort.requestConfirm(request);

            /* ============ 이 시점에서 Toss 결제는 완료, 각종 상황 대비 추후 스케줄링 재시도 복구 로직 필요 ============ */

            tossPaymentFinalizerUseCase.finalizeSuccess(paymentEvent, confirmResponse);
            return confirmResponse;
        }
        /*
           Exception 1 -> 결제 검증 및 승인 요청 중 발생
           - PaymentConfirmException 는 TossApiClient 의 onStatus() 에서 필터링되어 이곳에서 예외가 Catch 된다.
        */
        catch (TossPaymentConfirmException e) {
            if (e.getErrorCode().isRetryableError()) {
                /* Toss 재시도 가능한 오류 발생으로 UNKNOWN 상태로 변경되어 재시도 로직이 가능하도록 상태가 변경된다. */
                tossPaymentExecutorUseCase.markAsUnknown(paymentEvent);
                throw new TossPaymentException(RETRYABLE_ERROR);
            }
            else {
                /* Toss 재시도 불가능한 오류 */
                tossPaymentExecutorUseCase.markAsFail(paymentEvent);
            }
            throw new TossPaymentException(NON_RETRYABLE_ERROR);
        }
        /* Exception 2 -> 결제 성공 이후 비즈니스 로직 실패 */
        catch (BusinessException e) {
            tossPaymentExecutorUseCase.markAsSuccessButBusinessFailed(paymentEvent);
            throw new TossPaymentException(NON_RETRYABLE_ERROR);
        }
        /* Exception 3 -> 결제 승인 중 예상치 못한 예외 발생 */
        catch (Exception e) {
            tossPaymentExecutorUseCase.markAsFail(paymentEvent);
            throw e;
        }
    }
}
