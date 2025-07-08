//package com.demo.matching.payment.infrastructure.interceptor;
//
//import com.demo.matching.payment.domain.toss.exception.TossPaymentConfirmException;
//import com.demo.matching.payment.domain.toss.exception.TossPaymentTimeoutException;
//import org.springframework.http.HttpRequest;
//import org.springframework.http.client.ClientHttpRequestExecution;
//import org.springframework.http.client.ClientHttpRequestInterceptor;
//import org.springframework.http.client.ClientHttpResponse;
//
//import java.io.IOException;
//
//public class PaymentExceptionInterceptor implements ClientHttpRequestInterceptor {
//    @Override
//    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
//        try {
//            return execution.execute(request, body);
//        } catch (IOException e) {
//            throw new TossPaymentTimeoutException(e);
//        } catch (Exception e) {
//            throw new TossPaymentConfirmException(e);
//        }
//    }
//}
