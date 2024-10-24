package com.example.lambdatemplate.api.Handler;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.lambdatemplate.config.LambdaConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.lambdatemplate.service.CheckStatus;


public class LambdaHandler implements RequestHandler<Object, Object> {
    private CheckStatus checkStatus;

    public LambdaHandler() {
        //check if we need thease two later
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LambdaConfig.class); //Set the application Configuration
        context.scan("com.example.lambdatemplate"); //Manually Tell Spring to Scan all contents of the project for Dependency Injection
        checkStatus = context.getBean(CheckStatus.class);
    }

    @Override
    public Object handleRequest(final Object input, final Context context) {
        checkStatus.downloadLogFilesFromS3("logging-event-driven-bucket-1220-16492640", "Testing/Test.csv");
        //Add additional Service Methods Here
        return "Status Check Successfully Occured";
    }
}
