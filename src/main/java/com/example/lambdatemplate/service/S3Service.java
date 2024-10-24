package com.example.lambdatemplate.service;

import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
    private static final Logger log = LoggerFactory.getLogger(S3Service.class);
    private final S3Client s3Client;

    public S3Service(){
        this.s3Client = S3Client.builder()
                                .region(Region.US_EAST_2) //Change to Repo Secret Later
                                .build();
    }
    public void uploadFile(String bucketNAME, String keyName, String filePath){
        try{
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketNAME)
                .key(keyName)
                .build();
                s3Client.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(filePath)));
                log.info("File Uploaded to S3!");
        } catch (S3Exception e) {
            log.error("Error occured while uploading file to S3");
        }
    }
    //Method to download the file from S3
    public ResponseInputStream<GetObjectResponse> getObject(GetObjectRequest getObjectRequest){
        try{
            return s3Client.getObject(getObjectRequest);
        } catch (S3Exception e) {
            log.error("Error occured while fetching object from S3", e);
            throw e;
        }
    }

}