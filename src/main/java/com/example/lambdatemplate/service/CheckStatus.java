package com.example.lambdatemplate.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.S3Client;
import com.example.lambdatemplate.service.EmailOnError;

@Component
public class CheckStatus {
    private static final Logger log = LoggerFactory.getLogger(CheckStatus.class);
    private final S3Client s3Client;
    private final EmailOnError emailOnError;

    public CheckStatus(S3Client s3Client, EmailOnError emailOnError){
        this.s3Client = s3Client;
        this.emailOnError = emailOnError;
    }
    //Download Existing S3 Logging File
    public String downloadLogFilesFromS3(String bucketName, String logFileKey){
        try{
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(logFileKey)
                .build();
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);

            //Convert response object to string for viewing -> may not be required
            List<String> fileContent = new BufferedReader(new InputStreamReader(s3Object))
                .lines()
                .collect(Collectors.toList());

            if (!fileContent.isEmpty()){
                log.info("This is the content of the donwloaded S3 Bucket" + fileContent); //Print the file context
                String status = fileContent.get(fileContent.size() - 1);
                log.info("The Status is: " + status);
                if (status.contains("Success")){
                    log.info("Prior Application Succeeded");
                    //Add in code here to trigger the next lambda function
                } else {
                    log.info("Prior Applicaiton Failed");
                    //Add in code here to trigger an email as we will have an error message
                    //Prevent next service form kicking off
                }
                return status;
            } else {
                log.warn("No Records were found in the log file");
                //Email method add here for email sending for Missing file errors - this will only be if the logging file is empty or non existant
                //body of message is custom message
                //include as an attachment the 
                return "No Records Found";
            }
            
        } catch (S3Exception e) {
            log.error("Log file not found, creating a new log file.", e);
            return ""; // Return empty if log file not found
        }
    }

}
