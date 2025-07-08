package com.demo.matching.payment.infrastructure.toss.api;

import com.demo.matching.payment.application.toss.port.in.TossApiClientPort;
import com.demo.matching.payment.common.toss.exception.enums.TossPaymentConfirmErrorCode;
import com.demo.matching.payment.domain.toss.dto.TossPaymentInfo;
import com.demo.matching.payment.domain.toss.exception.TossPaymentConfirmException;
import com.demo.matching.payment.infrastructure.toss.dto.TossConfirmApiResponse;
import com.demo.matching.payment.infrastructure.toss.dto.TossPaymentFailOutput;
import com.demo.matching.payment.infrastructure.toss.mapper.TossInfoMapper;
import com.demo.matching.payment.infrastructure.toss.properties.TossProperties;
import com.demo.matching.payment.presentation.toss.request.TossConfirmRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static com.demo.matching.payment.infrastructure.toss.properties.TossSettingsProperties.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * RestClient가 왜 좋을까?
 * https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
 */
@Slf4j
@Component
public class TossApiClient implements TossApiClientPort {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final TossProperties tossProperties;

    public TossApiClient(TossProperties tossProperties, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.tossProperties = tossProperties;
        this.restClient = RestClient.builder()
                .requestFactory(createPaymentRequestFactory())
                .baseUrl(tossProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, createPaymentAuthHeader(tossProperties.getSecretKey()))
                .build();
    }

    /* 승인 전 paymentKey 로 Toss 에 조회하여 사용자 정보와 일치하는지 검증 정보 GET 요청 */
    @Override
    public TossPaymentInfo findPaymentByPaymentKey(String paymentKey) {
        TossConfirmApiResponse tossResponse = restClient.get()
                .uri(tossProperties.getValidEndpoint() + "/" + paymentKey)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new TossPaymentConfirmException(getPaymentConfirmErrorCode(response));
                })
                .body(TossConfirmApiResponse.class);

        return TossInfoMapper.from(tossResponse);
    }

    /* Toss 에 최종 승인 요청 */
    public TossPaymentInfo requestConfirm(TossConfirmRequest tossConfirmRequest) {
        TossConfirmApiResponse tossConfirmApiResponse = restClient.post()
                .uri(tossProperties.getConfirmEndpoint())
                .contentType(APPLICATION_JSON)
                .body(Map.of(
                        "paymentKey", tossConfirmRequest.paymentKey(),
                        "orderId", tossConfirmRequest.orderId(),
                        "amount", tossConfirmRequest.amount())
                )
                .retrieve()
                /* 4xx, 5xx 에러 응답을 한번에 잡기 위한 설정 -> Toss 가 code, message 반환함 */
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    /* 여기서 재시도 할 수 있는 결제 건 인지 아닌지 판단 */
                    throw new TossPaymentConfirmException(getPaymentConfirmErrorCode(response));
                })
                .body(TossConfirmApiResponse.class);

        return TossPaymentInfo.from(tossConfirmApiResponse);
    }

    /**
     * Toss는 에러 발생 시 code, message 에러 객체 응답하여 PaymentConfirmErrorCode 에서 관리
     */
    private TossPaymentConfirmErrorCode getPaymentConfirmErrorCode(ClientHttpResponse response) throws IOException {
        TossPaymentFailOutput confirmFailResponse = objectMapper.readValue(
                response.getBody(), TossPaymentFailOutput.class);

        /* Toss 에서 지정되어 있는 에러 Enum 값 중 일치 하는 것 필터링 */
        return TossPaymentConfirmErrorCode.findByName(confirmFailResponse.code());
    }

    private BufferingClientHttpRequestFactory createPaymentRequestFactory() {
        HttpComponentsClientHttpRequestFactory baseFactory = new HttpComponentsClientHttpRequestFactory();
        baseFactory.setConnectTimeout(CONNECT_TIMEOUT_SECONDS.getTime());
        baseFactory.setReadTimeout(READ_TIMEOUT_SECONDS.getTime());

        // Buffering으로 래핑해서 응답 바디를 여러 번 읽을 수 있게 함
        return new BufferingClientHttpRequestFactory(baseFactory);
    }

    private String createPaymentAuthHeader(String secretKey) {
        return AUTH_HEADER_PREFIX.getPrefix() + Base64.getEncoder()
                .encodeToString((secretKey + BASIC_DELIMITER.getPrefix()).getBytes(StandardCharsets.UTF_8));
    }
}
