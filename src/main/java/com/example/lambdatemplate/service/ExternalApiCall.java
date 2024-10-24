package com.example.lambdatemplate.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.lambdatemplate.api.model.Model;

@Service
public class ExternalApiCall {

    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(ExternalApiCall.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final S3Service s3Service;
    private final S3LoggingService s3LoggingService;

    public ExternalApiCall(RestTemplate restTemplate, S3Service s3Service, S3LoggingService s3LoggingService){
        this.restTemplate = restTemplate;
        this.s3Service = s3Service;
        this.s3LoggingService = s3LoggingService;
    }

    public Object getFact() throws JsonMappingException, JsonProcessingException{
        String url = "<Your API URL>";
        Object result = null;
        String fileName = "<Your file name here with extension>";
        String bucketName = "<Your AWS S3 Bucket Here>"; //Add this as repo secret in the future
        String s3Key = "<Directory Path in S3 for Data Landing If Applicable>" + fileName;
        String logFileKey = "<Directory in your Logging S3 Where the file is>/<Your Logging File>.<Logging File Extension>";

        try{
            String jsonResponse = restTemplate.getForObject(url, String.class); //This Actually Executes the API Call
            JsonNode jsonNode = objectMapper.readTree(jsonResponse); //Put the Response to an object for us to check if array or object
            if (jsonNode.isArray()){ //Get Response if List
                result = objectMapper.readValue(jsonResponse, new TypeReference<List<Model>>() {});
                log.info("Response is a List: " + result);
                log.info("Response Recieved from API: " + jsonResponse);
            } else if (jsonNode.isObject()){ //Get response if Object
                result = objectMapper.readValue(jsonResponse, Model.class);
                log.info("Response is an Object: " + result);
                log.info("Response Recieved from API: " + jsonResponse);
            } else {
                log.error("Response recieved from API is not a Json object, or Json List");
            }
            saveToFile(result, fileName);//Save to Temp Directory
            s3Service.uploadFile(bucketName, s3Key, "/tmp/" + fileName); //Upload to S3
            s3LoggingService.logMessageToS3("Succcess: Success occured at: " + LocalDateTime.now() + " On: <Your Service Name Here>", logFileKey);
        } catch (HttpStatusCodeException e) {
            log.error("Recieved Error from API", e.getResponseBodyAsString(), e);
            s3LoggingService.logMessageToS3("Error: Error occured at: " + LocalDate.now() + " On: <Your Service Name Here>", logFileKey);
            return null; //Exit the service if we get an error
        }
        return result;
    } 
    public void saveToFile(Object data, String fileName){
        String fullPath = "/tmp/" + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))){
            writer.write(objectMapper.writeValueAsString(data));
            log.info("File Successfuly Saved:" + fullPath);
        } catch (IOException e){
            log.error("Error while saving Json respons eot file", e);
        }
    }
}
