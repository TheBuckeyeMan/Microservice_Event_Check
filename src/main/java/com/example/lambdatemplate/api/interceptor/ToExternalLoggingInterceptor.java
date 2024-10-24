package com.example.lambdatemplate.api.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class ToExternalLoggingInterceptor implements ClientHttpRequestInterceptor{
    private static final Logger log = LoggerFactory.getLogger(ToExternalLoggingInterceptor.class);

    @Override
    @SuppressWarnings("null")
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequestDetails(request, body);//Log Details of request
        return execution.execute(request, body); //proceed with request
    }

    private void logRequestDetails(HttpRequest request, byte[] body) throws IOException {
        String requestBody = new String(body, "UTF-8"); //Convert request body to json format
        //Log the Request Details
        log.info("Outbound Request URL: {}", request.getURI());
        log.info("Outbound Request Method: {}", request.getMethod());
        log.info("Outbound Request Headers: {}", request.getHeaders()); //Authentication
        log.info("Outbound Request Body: {}", requestBody);
    }
}
