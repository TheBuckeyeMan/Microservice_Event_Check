package com.example.lambdatemplate.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        
        mailSender.setUsername("ajindustrytech@gmail.com");
        mailSender.setPassword("bljw yajy bsbq npqz");  // Replace with your Gmail App Password
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
