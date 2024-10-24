package com.example.lambdatemplate.api.interceptor;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.stereotype.Component;


@Component
public class ApiKeyInterceptor implements ClientHttpRequestInterceptor{
    
    private final String apikey;

    public ApiKeyInterceptor(String apikey){
        this.apikey = apikey;
    }

    @SuppressWarnings("null")
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().set("<Api Key Name>", apikey);
        return execution.execute(request, body);
    }
}
