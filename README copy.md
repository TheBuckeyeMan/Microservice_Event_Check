# Application Overview  

## API  
### Handler  
#### LambdaHandler  
Lambda Handler is responsable for being the entrypoint to the application when deployed via AWS Lambda  
    - handleRequest is the entrypoint method for the application itself  
    - Context Scan is added to ensure all spring beans are properly managed in the application.  

### Interceptor  
#### ApiKeyInterceptor  
The ApiKEyInterceptor is responsable for intercepting the HTTP Request before the external API GEts it, then, Adds a header to the request  
    - Responsable for adding API Key to authenticate us with external service  
    - IF external API Does not require API Key, we can comment this section out  

#### ToExternaLoggingINterceptor  
The ToExternaLoggingINterceptor is responsable for grabbing the services request to the HTTP API and logging the intial request so we can see for debudding purposes  
    - Not required for production  

#### IncommingLoggingInterceptor  
The IncomingLoggingInterceptor is responsable for grabbing the response from the HTTP API and logging the response so we can see for debudding purposes  
    - Not required for production  

### Model  
#### Model  
The model is required in order for us to model the response form the API. It is best practice to have a model to model the response and work with the data that way.  

## Config  
#### LambdaConfig  
This file contains all of the contents required for the lambda configuration  
    - Sets API Key value via Repo Secrets to the apiKey value  
    - Configures out Interceptors to be present and avalaible in the restTemplate used for sending the request.  

## Service  
Services are responsable for the "Business Logic" of the application -> The actual, lets do something in the application.  
#### ExternalApiCall  
This Service class is responsable for initiating the HttpRequest, Modeling the response, then saving that model to file.  
    - It also calls the S3Service(See next) to save the file we just created to the specified AWS S3 Bucket  
#### S3Service  
This service class is responsable for creating the emthod to upload the created file to a specified S3 bucket  

##  Resources  
#### Applcation.yml  
The purpose of application.yml is to provide the required environment configurations.  
    - API Key for this app is configured here  

# Testing  
Mock tests of the API Call are provided in this repo to ensure code coverage standards are met.  

## Notes on Testing:  
### Attributes of Testing:  
@Mock: Signifies a "Mock" Object and the purpose is to simulate real dependencies durring testing.  
@InjectMocks: Annotationn is used to inject all @Mock objects into the new Object Instance.  
@BeforeEach: Tells JUnit to run this method before each test. @BeforeEach is used to set up the test environments  
    - This is also responsable for initialize all of the mock objects annotated with @Mock  
@Test: Marks a method from our actual class for testing utilizing the JUnit testing. - This is UNIT TESTING  

### Assersions - Actual Tests:  
assertEquals("Test Value","Means of getting said value"): This tests if the mock is returning the expected value to us, the expected value is the "Test Value"  
assertNotNull("Value"): Checks to ensure that the value we want to check for in our mock situation is not null  
verify("method to test",times("number of times")): This tests that the mock execution executes as many tiems as we expect it to  

# Reading the Tests  
## The following relates to ApiKeyInterceptorTest.java  
    -  Purpose is to test the ApiKeyInterceptor class  
        - We pass in a test key in the "Test Your API Key Here -> Do NOT Commit" area. We will leverage this for our testing  
            - DO NOT COMMIT YOUR ACTUAL API KEY TO GITHUB  
                - when(httpRequest.getHeaders()).thenReturn(headers): Tells the mock to get new headers when the .getHEaders() method is called  
                - when(clientHttpRequestExecution.execute(any(), any())).thenReturn(clientHttpResponse): Sets up the Mock clientHttpResponse when execute() method is called -> This mocks the actual HTTP Request  
                - ClientHttpResponse response = apiKeyInterceptor.intercept(httpRequest, null, clientHttpRequestExecution): the intercept() method adds the api key to the header. Objects passed are.  
                    - httpRequest: Mock object simulating an outgoing request  
                    - null: a null body for simplicity  
                    - clientHttpRequestExecution: Mock object simulating the execution of the request.  

## The following relates to ExternalApiCallTest.java  
    - Purpose is to test the ExternalApiCallTest Class  
        - restTemplate.getForObject(anyString(), eq(String.class)): Simulates a HTTP GET Request to return a Json Response  
        - objectMapper.readTree(jsonResponse): Simulates parsing the JsonResponse into the JsonNode  
        - objectMapper.readValue(anyString(), any(TypeReference.class)): Simulates mapping the JsonString to a list of Model objects  
        - externalApiCall.getFact(): expected to make an HttpRequest, parse the response, and return the result.    
        - assertNotNull(result): ensures that the response from the api is not null  
        - verify(s3Service, times(1)).uploadFile(anyString(), anyString(), anyString()) Verifies that the uploadFile method of S3Service was called exactly 1 time.  
        - restTemplate.getForObject(anyString(), eq(String.class)) Simulates throwing a HTTP 500 internal server error  
        - externalApiCall.getFact() is expected to return null as we are simulating a failure.  
        - verify(s3Service, never()).uploadFile(anyString(), anyString(), anyString()): verifies that the method of uploadFile was never called since we had a failure from the API  
## The Following Relates to S3ServiceTEst.java  
    - Purpose is to test the S3Service Class  
        - s3ClientField.setAccessible(true), s3ClientField.set(s3Service, s3Client): Added to @BeforeEach to alow test to properly modify the S3Service since it is made as a consuctor in the S3Service class    
        - Path testFilePath = Paths.get("/tmp/test-file.json"), Files.createFile(testFilePath): Just set up a temporary testing file for the mock  
        - Params: We define the params in this mock scenario -> DO NOT NEED to be actual params  
        - when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(PutObjectResponse.builder().build()): tells mockito to return a successful PutObjectResponse whenever putObject is called wioth any PutObjectRequest  
        - s3Service.uploadFile(bucketname, keyName, filePath): mocks the action of acutally uploading the file to s3->It wont actually as its a mock  
        - verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class)): checks that the putObject method od the S3Client was called exactly 1 time in the mock  

## target
The target directory is generated after you run a mvn compile dependency:copy-dependencies -DincludeScope=runtime
    - The target directory will contain our executable jar file

## Other Files  
#### .dockerignore  
This file tels docker to exclude specific files if requested  

#### .gitignore  
Tells git to not save specified files to the state file, and will NOT be passed to github  
    - Should include all large files and executables  

#### docker-compose.yml  
Alows you to leverage multiple container builds locally for testing purloses if you have various microservices.  

#### Dockerfile  
File that packages our application into a container. This is required for the framework we leverage in this template to deploy to aws lambda  

#### POM.xml  
This file helps provide a base for our application. It includes dependencies, which are like python packages that need to be included within the project.  
 
#### Readme  
You are on readme, nuff said  

#### Template.yml  
Auto generated while following AWS Lambda for java tutorial, will look to remove in the fuiture as I dont think its required.  