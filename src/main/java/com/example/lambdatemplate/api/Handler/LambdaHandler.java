package com.example.lambdatemplate.api.Handler;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.lambdatemplate.config.LambdaConfig;
import com.example.lambdatemplate.service.ExternalApiCall;
import com.fasterxml.jackson.core.JsonProcessingException;

public class LambdaHandler implements RequestHandler<Object, Object> {
    private ExternalApiCall externalApiCall;

    public LambdaHandler() {
        //check if we need thease two later
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LambdaConfig.class); //Set the application Configuration
        context.scan("com.example.lambdatemplate"); //Manually Tell Spring to Scan all contents of the project for Dependency Injection
        externalApiCall = context.getBean(ExternalApiCall.class);
    }

    @Override
    public Object handleRequest(final Object input, final Context context) {
        try{
            externalApiCall.getFact();
            //Add additional Service Methods Here
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "API Successfully Triggered! File Saved to S3 Bucket!";
    }
}
