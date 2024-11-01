package com.example.lambdatemplate.api.Handler;

import java.io.IOException;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.env.PropertySource;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.lambdatemplate.config.LambdaConfig;
import com.example.lambdatemplate.service.CheckStatus;


public class LambdaHandler implements RequestHandler<Object, Object> {
    private CheckStatus checkStatus;

    public LambdaHandler() {
        // //check if we need thease two later
        // AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LambdaConfig.class); //Set the application Configuration
        // context.scan("com.example.lambdatemplate"); //Manually Tell Spring to Scan all contents of the project for Dependency Injection
        // checkStatus = context.getBean(CheckStatus.class);
        // Create a standard environment and load application.yml into it
        StandardEnvironment environment = new StandardEnvironment();
        try {
            // Manually load application.yml from src/main/resources
            YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
            PropertySource<?> yamlProperties = loader.load("application.yml", new ClassPathResource("application.yml")).get(0);
            environment.getPropertySources().addLast(yamlProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize Spring context with LambdaConfig and custom environment
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setEnvironment(environment); // Set the environment before refresh
        context.register(LambdaConfig.class); // Register the configuration class
        context.scan("com.example.lambdatemplate"); // Scan for beans in your package
        context.refresh(); // Only needed once, at initialization

        // Retrieve the CheckStatus bean
        checkStatus = context.getBean(CheckStatus.class);

    }

    @Override
    public Object handleRequest(final Object input, final Context context) {
        try {
            checkStatus.downloadLogFilesFromS3("<Your s3 bucket here>", "<Directory of file>/<Name of file>.<file Extension>");
        } catch (IOException e) {
            e.printStackTrace();
        }
   
        //Add additional Service Methods Here
        return "Status Check Successfully Occured";
    }
}
