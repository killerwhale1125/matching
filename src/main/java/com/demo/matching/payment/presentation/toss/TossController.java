package com.demo.matching.payment.presentation.toss;

import com.demo.matching.core.common.exception.BusinessResponse;
import com.demo.matching.payment.presentation.toss.port.in.TossCheckoutService;
import com.demo.matching.payment.presentation.toss.port.in.TossConfirmService;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import com.demo.matching.payment.presentation.toss.request.TossCheckoutRequest;
import com.demo.matching.payment.presentation.toss.response.TossConfirmResponse;
import com.demo.matching.payment.presentation.toss.response.TossCheckoutResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/toss")
public class TossController {
    private final TossConfirmService tossConfirmService;
    private final TossCheckoutService tossCheckoutService;

    @PostMapping("/checkout")
    public BusinessResponse<TossCheckoutResponse> checkout(@RequestBody @Valid TossCheckoutRequest request) {
        return new BusinessResponse(tossCheckoutService.checkoutPayment(request));
    }

    /**
     * @param request ( memberId, paymentKey, orderId, amount ) -> Client 는 memberId 도 반드시 함께 보내야 한다.
     * 1. checkout API 를 통해 orderId를 만들어주었으며, Client 가 Toss 에 결제 요청 후 완료 시 리다이렉션을 통해 아래 success API(승인하기 버튼) 호출
     * 2. 서버에서 결제 인증 단계에서 토스페이먼츠에 저장된 결제 정보 조회
     * 3. 서버에서 결제 정보와 클라이언트에서 받은 결제 정보, DB에 저장된 주문 정보 검증
     * 4. 결제 정보에 이상이 없다면 토스에 결제 승인 요청
     * 5. 결제 완료로 DB 업데이트
     * 6. 클라이언트에게 성공 내역 반환
     */
    @PostMapping("/confirm")
    public BusinessResponse<TossConfirmResponse> confirm(@RequestBody @Valid TossConfirmRequest request) {
        return new BusinessResponse<>(tossConfirmService.confirmPayment(request));
    }
}
