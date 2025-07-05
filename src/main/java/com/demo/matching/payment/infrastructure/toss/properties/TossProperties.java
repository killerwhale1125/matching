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
}
