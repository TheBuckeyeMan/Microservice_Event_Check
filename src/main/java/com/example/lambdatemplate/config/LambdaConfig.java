package com.example.lambdatemplate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ComponentScan(basePackages = "com.example.lambdatemplate") //Since its lambda, we need to Manually tell spring to scan all required dependencies for injection
public class LambdaConfig {

    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
            .region(Region.US_EAST_2)
            .build();
    }
}