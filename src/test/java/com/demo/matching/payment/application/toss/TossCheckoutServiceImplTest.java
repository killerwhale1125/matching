package com.demo.matching.payment.application.toss;

import com.demo.matching.core.common.exception.BusinessException;
import com.demo.matching.core.common.exception.BusinessResponseStatus;
import com.demo.matching.core.common.service.port.UUIDProvider;
import com.demo.matching.payment.application.toss.port.in.TossPaymentEventRepository;
import com.demo.matching.payment.application.usecase.OrderedMemberUseCase;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.dto.MemberInfo;
import com.demo.matching.payment.infrastructure.toss.properties.TossProperties;
import com.demo.matching.payment.presentation.port.in.TossCheckoutService;
import com.demo.matching.payment.presentation.toss.request.TossCheckoutRequest;
import com.demo.matching.payment.presentation.toss.response.TossCheckoutResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.demo.matching.core.common.exception.BusinessResponseStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class TossCheckoutServiceImplTest {

    private TossCheckoutService tossCheckoutService;
    private TossPaymentEventRepository tossPaymentEventRepository;
    private OrderedMemberUseCase orderedMemberUseCase;
    private UUIDProvider uuidProvider;

    private final TossProperties tossProperties = new TossProperties(
            "secretKey", "clientKey", "baseUrl", "successUrl", "failUrl", "confirmEndpoint", "validEndpoint"
    );

    @BeforeEach
    void setUp() {
        tossPaymentEventRepository = mock(TossPaymentEventRepository.class);
        orderedMemberUseCase = mock(OrderedMemberUseCase.class);
        uuidProvider = mock(UUIDProvider.class);

        // UUID 무조건 같은값 리턴
        when(uuidProvider.generateUUID()).thenReturn("UUID");

        // 회원 ID가 1인 경우 정상 반환
        when(orderedMemberUseCase.getMemberInfoById(1L))
                .thenReturn(new MemberInfo(1L));

        // 회원 ID가 2인 경우 예외 발생
        when(orderedMemberUseCase.getMemberInfoById(2L))
                .thenThrow(new BusinessException(MEMBER_NOT_FOUND)); // 실제로는 BusinessException 등

        tossCheckoutService = new TossCheckoutServiceImpl(
                tossPaymentEventRepository,
                tossProperties,
                orderedMemberUseCase,
                uuidProvider
        );
    }

    @Test
    @DisplayName("포인트 충전 금액, 주문명, 회원 아이디로 주문 정보를 생성한다.")
    void checkoutPayment_success() {
        // given
        final long amount = 1000;
        final String orderName = "orderName";
        final String memberId = "1";
        final TossCheckoutRequest tossCheckoutRequest = new TossCheckoutRequest(amount, orderName, memberId);

        // when
        TossCheckoutResponse result = tossCheckoutService.checkoutPayment(tossCheckoutRequest);

        // then
        assertThat(result.orderId()).isEqualTo("UUID");
        assertThat(result.amount()).isEqualTo(amount);
        assertThat(result.orderName()).isEqualTo(orderName);
        assertThat(result.memberId()).isEqualTo(Long.valueOf(memberId));
        assertThat(result.successUrl()).isEqualTo(tossProperties.getSuccessUrl());
        assertThat(result.failUrl()).isEqualTo(tossProperties.getFailUrl());
        assertThat(result.clientKey()).isEqualTo(tossProperties.getClientKey());
    }

    @Test
    @DisplayName("회원 아이디가 없으면 결제 정보 생성에 실패한다.")
    void checkoutPayment_fail_not_found_member() {
        // given
        final long amount = 1000;
        final String orderName = "orderName";
        final String memberId = "2";
        final TossCheckoutRequest tossCheckoutRequest = new TossCheckoutRequest(amount, orderName, memberId);

        // when
        assertThatThrownBy(() -> tossCheckoutService.checkoutPayment(tossCheckoutRequest))
                .isInstanceOf(BusinessException.class);
    }
}
