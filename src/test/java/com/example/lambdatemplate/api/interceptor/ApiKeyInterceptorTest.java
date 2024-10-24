package com.example.lambdatemplate.api.interceptor;

import java.io.IOException;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

public class ApiKeyInterceptorTest {
    private ApiKeyInterceptor apiKeyInterceptor;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private ClientHttpRequestExecution clientHttpRequestExecution;

    @Mock
    private ClientHttpResponse clientHttpResponse;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        apiKeyInterceptor = new ApiKeyInterceptor("<Test Your API Key Here -> Do NOT Commit>");
    }

    @Test
    public void testInterceptSetsApiKeyHeader() throws IOException{
        HttpHeaders headers = new HttpHeaders();
        when(httpRequest.getHeaders()).thenReturn(headers);
        when(clientHttpRequestExecution.execute(any(), any())).thenReturn(clientHttpResponse);

        ClientHttpResponse response = apiKeyInterceptor.intercept(httpRequest, null, clientHttpRequestExecution);

        assertEquals("<Test Your API Key Here -> Do NOT Commit>", headers.getFirst("<Api Key Name, not the API Key Itself>"));
        verify(clientHttpRequestExecution, times(1)).execute(any(), any());
    }
}