package com.demo.matching.payment.domain;

import com.demo.matching.core.common.exception.BusinessException;
import com.demo.matching.payment.domain.toss.exception.TossPaymentException;
import com.demo.matching.payment.domain.toss.TossPaymentEvent;
import com.demo.matching.payment.domain.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.presentation.toss.request.TossCheckoutRequest;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static com.demo.matching.payment.domain.toss.enums.TossPaymentConfirmResultStatus.SUCCESS;
import static com.demo.matching.payment.domain.toss.enums.TossPaymentStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TossPaymentEventTest {
    @Test
    @DisplayName("정상적으로 TossPaymentEvent를 생성한다")
    void create_success() {
        // given
        final TossCheckoutRequest request = new TossCheckoutRequest(1000, "orderName", "1");

        // when
        final TossPaymentEvent event = TossPaymentEvent.create("order123", 1L, request);

        // then
        assertThat(event.getBuyerId()).isEqualTo(1L);
        assertThat(event.getAmount()).isEqualTo(1000);
        assertThat(event.getTossPaymentStatus()).isEqualTo(READY);
    }

    @Test
    @DisplayName("결제 시작 시 상태가 READY, IN_PROGRESS, UNKNOWN 여야 한다 - 아닌 경우 예외")
    void startConfirm_invalidStatus_throws() {
        // given
        final TossPaymentEvent event = TossPaymentEvent.builder()
                .amount(1000)
                .tossPaymentStatus(DONE) // 잘못된 상태
                .build();

        // then
        assertThatThrownBy(() -> event.startConfirmAndUpdate("paymentKey", 1000, LocalDateTime.now()))
                .isInstanceOf(TossPaymentException.class);
    }

    @Test
    @DisplayName("결제 시작 시 금액이 일치하지 않으면 예외")
    void startConfirm_invalidAmount_throws() {
        // given
        final TossPaymentEvent event = TossPaymentEvent.builder()
                .amount(1000)
                .tossPaymentStatus(READY)
                .build();

        // then
        assertThatThrownBy(() -> event.startConfirmAndUpdate("paymentKey", 2000, LocalDateTime.now()))
                .isInstanceOf(TossPaymentException.class);
    }

    @Test
    @DisplayName("성공 처리 시 상태가 DONE 으로 변경된다")
    void success_setsApproved() {
        // given
        final TossPaymentEvent event = TossPaymentEvent.builder()
                .tossPaymentStatus(IN_PROGRESS)
                .build();
        final TossPaymentInfo info = new TossPaymentInfo(
            "paymentKey",
                "order123",
                "orderName",
                "신용카드",
                1000,
                "IN_PROGRESS",
                OffsetDateTime.now().toString(),
                OffsetDateTime.now().toString(),
                SUCCESS
        );

        // when
        event.success(info);

        // then
        assertThat(event.getTossPaymentStatus()).isEqualTo(DONE);
        assertThat(event.getApprovedAt()).isNotNull();
    }

    @Test
    @DisplayName("재시도가 가능한 상태(READY, IN_PROGRESS, UNKNOWN)에서 호출 시 " +
            "UNKNOWN 으로 재시도 가능 상태로 변경한다.")
    void unknown_success() {
        // given
        final TossPaymentEvent ready = TossPaymentEvent.builder()
                .tossPaymentStatus(READY)
                .build();

        final TossPaymentEvent inProgress = TossPaymentEvent.builder()
                .tossPaymentStatus(IN_PROGRESS)
                .build();

        final TossPaymentEvent unknown = TossPaymentEvent.builder()
                .tossPaymentStatus(UNKNOWN)
                .build();

        // when
        ready.unknown();
        inProgress.unknown();
        unknown.unknown();

        // then
        assertThat(ready.getTossPaymentStatus()).isEqualTo(UNKNOWN);
        assertThat(inProgress.getTossPaymentStatus()).isEqualTo(UNKNOWN);
        assertThat(unknown.getTossPaymentStatus()).isEqualTo(UNKNOWN);
    }

    @Test
    @DisplayName("UNKNOWN 처리가 불가능한 상태일 경우 예외 발생")
    void unknown_invalidStatus() {
        // given
        TossPaymentEvent done = TossPaymentEvent.builder()
                .tossPaymentStatus(DONE)
                .build();

        // when & then
        assertThatThrownBy(done::unknown)
                .isInstanceOf(TossPaymentException.class);
    }

    @Test
    @DisplayName("재시도 불가능한 상태일 경우 FAIL 처리된다")
    void fail_success() {
        // given
        final TossPaymentEvent event = TossPaymentEvent.builder()
                .tossPaymentStatus(DONE) // 재시도 불가능
                .build();

        // when
        event.fail();

        // then
        assertThat(event.getTossPaymentStatus()).isEqualTo(FAILED);
    }

    @Test
    @DisplayName("재시도 가능한 상태(IN_PROGRESS, UNKNOWN) 인 경우 취소할 수 없다.")
    void fail_retryable_shouldThrow() {
        final TossPaymentEvent inProgress = TossPaymentEvent.builder()
                .tossPaymentStatus(IN_PROGRESS)
                .executedAt(LocalDateTime.now().minusMinutes(3))
                .retryCount(0)
                .build();

        final TossPaymentEvent unknown = TossPaymentEvent.builder()
                .tossPaymentStatus(UNKNOWN)
                .executedAt(LocalDateTime.now().minusMinutes(3))
                .retryCount(0)
                .build();

        assertThatThrownBy(inProgress::fail)
                .isInstanceOf(TossPaymentException.class);

        assertThatThrownBy(unknown::fail)
                .isInstanceOf(TossPaymentException.class);
    }

    @Test
    @DisplayName("valid 호출 시 정보가 모두 일치하면 검증에 성공한다.")
    void valid_success() {
        // given
        final TossPaymentEvent event = TossPaymentEvent.builder()
                .orderId("order123")
                .paymentKey("paymentKey")
                .amount(1000)
                .buyerId(1L)
                .tossPaymentStatus(IN_PROGRESS)
                .build();
        final TossPaymentInfo info = new TossPaymentInfo("paymentKey", "order123", "name", "카드", 1000, "IN_PROGRESS", OffsetDateTime.now().toString(), OffsetDateTime.now().toString(), SUCCESS);
        final TossConfirmRequest request = new TossConfirmRequest(1L, "paymentKey", "order123", 1000);

        // when & then
        event.valid(info, request); // 예외 안 나면 성공
    }

    @Test
    @DisplayName("정보가 하나라도 일치하지 않는다면 검증에 실패한다.")
    void valid_invalidOrderId() {
        TossPaymentEvent event = TossPaymentEvent.builder()
                .orderId("order123")
                .paymentKey("paymentKey")
                .amount(1000)
                .buyerId(1L)
                .tossPaymentStatus(IN_PROGRESS)
                .build();

        TossPaymentInfo info = new TossPaymentInfo("paymentKey", "order123", "name", "카드", 1000, "IN_PROGRESS", OffsetDateTime.now().toString(), OffsetDateTime.now().toString(), SUCCESS);
        TossConfirmRequest diffReq1 = new TossConfirmRequest(2L, "paymentKey", "order123", 1000);
        TossConfirmRequest diffReq2 = new TossConfirmRequest(1L, "pk", "order123", 1000);
        TossConfirmRequest diffReq3 = new TossConfirmRequest(1L, "paymentKey", "od", 1000);
        TossConfirmRequest diffReq4= new TossConfirmRequest(1L, "paymentKey", "order123", 3000);

        assertThatThrownBy(() -> event.valid(info, diffReq1))
                .isInstanceOf(TossPaymentException.class);

        assertThatThrownBy(() -> event.valid(info, diffReq2))
                .isInstanceOf(TossPaymentException.class);

        assertThatThrownBy(() -> event.valid(info, diffReq3))
                .isInstanceOf(TossPaymentException.class);

        assertThatThrownBy(() -> event.valid(info, diffReq4))
                .isInstanceOf(TossPaymentException.class);
    }

    @Test
    @DisplayName("결제가 진행 중인 상태(IN_PROGRESS)가 아닐 경우 결제 비즈니스 로직이 실패 처리로 간주한다." +
            "-> DONE 일 경우 이미 포인트 충전이 완료됨 ( 포인트 충전 + 결제 정보 저장이 하나의 트랜잭션이기 때문 )")
    void businessFail_validState() {
        // given
        final TossPaymentEvent done = TossPaymentEvent.builder()
                .tossPaymentStatus(DONE)
                .build();

        // when
        done.businessFail();

        // then
        assertThat(done.getTossPaymentStatus()).isEqualTo(SUCCESS_BUT_BIZ_FAILED);
    }

    @Test
    @DisplayName("결제가 진행 중인 상태(IN_PROGRESS) 일 경우에만 비즈니스 로직 실패처리 되지 않고, 재시도 기회가 주어진다.")
    void businessFail_invalidState() {
        final TossPaymentEvent event = TossPaymentEvent.builder()
                .tossPaymentStatus(IN_PROGRESS)
                .build();

        assertThatThrownBy(event::businessFail)
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("재시도 조건 확인 - IN_PROGRESS 상태이고 시간이 지났을 때")
    void isRetryable_validCondition() {
        // given
        final LocalDateTime before = LocalDateTime.now().minusMinutes(4);
        final LocalDateTime now = LocalDateTime.now();
        final TossPaymentEvent event = TossPaymentEvent.builder()
                .tossPaymentStatus(IN_PROGRESS)
                .executedAt(before)
                .retryCount(3)
                .build();

        // when
        final boolean result = event.isRetryable(now);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("재시도 횟수 초과 시 false 반환")
    void isRetryable_overLimit() {
        // given
        final TossPaymentEvent event = TossPaymentEvent.builder()
                .tossPaymentStatus(IN_PROGRESS)
                .executedAt(LocalDateTime.now().minusMinutes(3))
                .retryCount(5) // 초과
                .build();

        // when
        final boolean result = event.isRetryable(LocalDateTime.now());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("increaseRetryCount 호출 시 횟수가 증가한다")
    void increaseRetryCount_success() {
        // given
        final TossPaymentEvent event = TossPaymentEvent.builder()
                .retryCount(2)
                .tossPaymentStatus(IN_PROGRESS)
                .build();

        // when
        event.increaseRetryCount();

        // then
        assertThat(event.getRetryCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("UNKNOWN, IN_PROGRESS 상태가 아닐 경우, 재시도를 더이상 할 수 없다.")
    void increaseRetryCount_invalidStatus() {
        // given
        final TossPaymentEvent event = TossPaymentEvent.builder()
                .retryCount(1)
                .tossPaymentStatus(READY)
                .build();

        // when & then
        assertThatThrownBy(event::increaseRetryCount)
                .isInstanceOf(TossPaymentException.class);
    }


}
