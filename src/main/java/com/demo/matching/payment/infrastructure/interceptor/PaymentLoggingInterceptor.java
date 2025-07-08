//package com.demo.matching.payment.infrastructure.interceptor;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpRequest;
//import org.springframework.http.client.ClientHttpRequestExecution;
//import org.springframework.http.client.ClientHttpRequestInterceptor;
//import org.springframework.http.client.ClientHttpResponse;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.util.stream.Collectors;
//
//@Slf4j
//public class PaymentLoggingInterceptor implements ClientHttpRequestInterceptor {
//
//    @Override
//    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
//        logRequest(request, body);
//
//        ClientHttpResponse response = execution.execute(request, body);
//        logResponse(response);
//
//        return response;
//    }
//
//    private void logRequest(HttpRequest request, byte[] body) {
//        log.info(">> [Toss API 요청]");
//        log.info(">> URI     : {}", request.getURI());
//        log.info(">> Method  : {}", request.getMethod());
//        log.info(">> Headers : {}", request.getHeaders());
//        log.info(">> Body    : {}", new String(body, StandardCharsets.UTF_8));
//    }
//
//    private void logResponse(ClientHttpResponse response) throws IOException {
//        String responseBody = new BufferedReader(
//                new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
//                .lines()
//                .collect(Collectors.joining("\n"));
//
//        log.info("<< [Toss API 응답]");
//        log.info("<< Status code : {}", response.getStatusCode());
//        log.info("<< Status text : {}", response.getStatusText());
//        log.info("<< Headers     : {}", response.getHeaders());
//        log.info("<< Body        : {}", responseBody);
//    }
//}
