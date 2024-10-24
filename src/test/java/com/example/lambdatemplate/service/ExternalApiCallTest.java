package com.example.lambdatemplate.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.lambdatemplate.api.model.Model;
import com.fasterxml.jackson.core.type.TypeReference;

public class ExternalApiCallTest {
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private S3Service s3Service;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ExternalApiCall externalApiCall;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    //Test a successful API Call
    @Test
    public void testGetFactReturnObject() throws Exception{
        String jsonResponse = "<Mock what you expect the ful json response to be here>";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(jsonResponse);
        when(objectMapper.readTree(jsonResponse)).thenReturn(mock(JsonNode.class));
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenReturn(Collections.singletonList(new Model("<Insert the test you expect it to return here>")));
            
        Object result = externalApiCall.getFact();
        assertNotNull(result);
        verify(s3Service, times(1)).uploadFile(anyString(), anyString(), anyString());
    }

    //Test an API Call that returns null -> So an error
    @Test
    public void testGetFactHandleError() throws Exception{
        when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenThrow(new HttpStatusCodeException(HttpStatus.INTERNAL_SERVER_ERROR) {
            private static final long serialVersionUID = 1L;
        });
        Object result = externalApiCall.getFact();
        assertNull(result);
        verify(s3Service, never()).uploadFile(anyString(), anyString(), anyString());
    }
}
