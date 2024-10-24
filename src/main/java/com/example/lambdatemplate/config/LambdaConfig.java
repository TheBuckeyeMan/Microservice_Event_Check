package com.example.lambdatemplate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import com.example.lambdatemplate.api.interceptor.ApiKeyInterceptor;
import com.example.lambdatemplate.api.interceptor.IncommingLoggingInterceptor;
import com.example.lambdatemplate.api.interceptor.ToExternalLoggingInterceptor;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;


@Configuration
@ComponentScan(basePackages = "com.example.lambdatemplate") //Since its lambda, we need to Manually tell spring to scan all required dependencies for injection
public class LambdaConfig {

    @Value("${Api_Key}")
    private String apikey;

    @Bean
    public ApiKeyInterceptor apiKeyInterceptor(){
        return new ApiKeyInterceptor(apikey);
    }

    @Bean
    public IncommingLoggingInterceptor incommingLoggingInterceptor(){
        return new IncommingLoggingInterceptor();
    }

    @Bean
    public ToExternalLoggingInterceptor toExternalLoggingInterceptor(){
        return new ToExternalLoggingInterceptor();
    }
    
    @Bean
    public RestTemplate restTemplate(ToExternalLoggingInterceptor toExternalLoggingInterceptor, ApiKeyInterceptor apiKeyInterceptor,IncommingLoggingInterceptor incommingLoggingInterceptor){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Arrays.asList(incommingLoggingInterceptor, apiKeyInterceptor, toExternalLoggingInterceptor));
        return restTemplate;
    }
}
