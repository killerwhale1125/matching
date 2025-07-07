package com.demo.matching.payment.infrastructure.toss.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "toss")
public class TossProperties {
    private String secretKey;
    private String clientKey;
    private String baseUrl;
    private String successUrl;
    private String failUrl;
    private String confirmEndpoint;
    private String validEndpoint;

    // 테스트 전용 생성자
    public TossProperties(String secretKey, String clientKey, String baseUrl,
                          String successUrl, String failUrl,
                          String confirmEndpoint, String validEndpoint) {
        this.secretKey = secretKey;
        this.clientKey = clientKey;
        this.baseUrl = baseUrl;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
        this.confirmEndpoint = confirmEndpoint;
        this.validEndpoint = validEndpoint;
    }

    // 기본 생성자도 있어야 합니다.
    public TossProperties() {
    }
}
