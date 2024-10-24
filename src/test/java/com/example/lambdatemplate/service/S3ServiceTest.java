package com.example.lambdatemplate.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class S3ServiceTest {

    @Mock
    private S3Client s3Client;  // Mock the S3Client

    @InjectMocks
    private S3Service s3Service;  // Inject the mock into S3Service

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Field s3ClientField = S3Service.class.getDeclaredField("s3Client"); // Use reflection to set the private 's3Client' field in S3Service to the mock s3Client
        s3ClientField.setAccessible(true);  // Make the private field accessible
        s3ClientField.set(s3Service, s3Client);  // Set the mock S3Client
    }

    @Test
    public void testUploadFile() {
        Path testFilePath = Paths.get("/tmp/test-file.json");
        try {
            Files.createFile(testFilePath); // Create a temporary file for testing

            String bucketname = "test-bucket";
            String keyName = "keyname";
            String filePath = testFilePath.toString();

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());// Mock the behavior of s3Client.putObject to return a successful response

            s3Service.uploadFile(bucketname, keyName, filePath);// Execute the method
            verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class)); // Verify that the s3Client putObject method was called with the correct parameters

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Clean up the temporary file after the test
                Files.deleteIfExists(testFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}