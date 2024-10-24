package com.example.lambdatemplate.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3LoggingService {
    private static final Logger log = LoggerFactory.getLogger(S3LoggingService.class);
    private final S3Service s3Service;

    public S3LoggingService(S3Service s3Service){
        this.s3Service = s3Service;
    }
    //Download Existing S3 File
    public String downloadLogFilesFromS3(String bucketName, String logFileKey){
        try{
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(logFileKey)
                .build();
            ResponseInputStream<GetObjectResponse> s3Object = s3Service.getObject(getObjectRequest);
            return new BufferedReader(new InputStreamReader(s3Object))
                .lines()
                .collect(Collectors.joining("\n"));
        } catch (S3Exception e) {
            log.error("Log file not found, creating a new log file.", e);
            return ""; // Return empty if log file not found
        }
    }
    //Append new log entry to the existing log file
    public String appendLogEntry(String existingContent, String newLogEntry){
        return existingContent + "\n" + newLogEntry;
    }

    //Upload the updated logging file to the logging S3 bucket
    public void uploadLogFileToS3(String bucketName, String logFileKey, String updatedContent){
        String fileName = "/tmp/log-file.txt";
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
            writer.write(updatedContent); //Write the updated content to the local file
            writer.flush(); // Ensure content is flushed to the file

            String fileContent = Files.readString(Paths.get(fileName)); // Read and log the file contents to verify
            log.info("File content before uploading to S3: \n" + fileContent);

            // Check file size after writing
            File file = new File(fileName);
            if (file.exists()) {
                log.info("File size after writing: " + file.length() + " bytes");
            } else {
                log.error("File not found: " + fileName);
            }
            s3Service.uploadFile(bucketName, logFileKey, fileName);
            log.info("Uploading log file to bucket: " + bucketName + " with key: " + logFileKey + "  by filename " + fileName);
            log.info("Log file successfuly uploaded to s3");
        } catch (IOException e) {
            log.error("Error while writing the log file to s3");
        }
    }
    //Log message to s3(This method calls other steps)
    public void logMessageToS3(String message, String logFileKey){
        String bucketName = "<Your Logging Bucket Name>";
        try{
            log.info("Message being logged: '" + message + "'");
            if (message == null || message.trim().isEmpty()) {
                log.warn("The message is either null or empty!");
            }
            String existingContent = downloadLogFilesFromS3(bucketName, logFileKey); //Download the existing Log File content
            String updatedContent = appendLogEntry(existingContent, message); //Append tge new log message
            uploadLogFileToS3(bucketName, logFileKey, updatedContent);
    } catch (Exception e){
        log.error("Error while logging log messages to S3", e);
        }
    }
}
