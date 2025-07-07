package com.demo.matching.payment.application.toss;

import com.demo.matching.core.common.service.port.UUIDProvider;
import com.demo.matching.payment.application.toss.port.in.TossPaymentEventRepository;
import com.demo.matching.payment.application.usecase.OrderedMemberUseCase;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.dto.MemberInfo;
import com.demo.matching.payment.infrastructure.toss.properties.TossProperties;
import com.demo.matching.payment.presentation.port.in.TossCheckoutService;
import com.demo.matching.payment.presentation.toss.request.TossCheckoutRequest;
import com.demo.matching.payment.presentation.toss.response.TossCheckoutResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TossCheckoutServiceImpl implements TossCheckoutService {

    private final TossPaymentEventRepository tossPaymentEventRepository;
    private final TossProperties tossProperties;
    private final OrderedMemberUseCase orderedMemberUseCase;
    private final UUIDProvider uuidProvider;

    @Override
    @Transactional
    public TossCheckoutResponse checkoutPayment(TossCheckoutRequest request) {
        /* 외부 Internal Port 를 통해 회원 정보를 조회 */
        MemberInfo member = orderedMemberUseCase.getMemberInfoById(
                Long.valueOf(request.memberId())
        );

        /* 결제 이벤트 정보 생성 */
        TossPaymentEvent tossPaymentEvent = TossPaymentEvent.create(uuidProvider.generateUUID(), member.memberId(), request);
        tossPaymentEventRepository.save(tossPaymentEvent);

        return TossCheckoutResponse.from(
                tossPaymentEvent,
                tossProperties.getSuccessUrl(),
                tossProperties.getFailUrl(),
                tossProperties.getClientKey()
        );
    }
}
