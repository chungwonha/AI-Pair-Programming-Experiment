package com.cool.app.newssentimentanalysis.service;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import java.nio.ByteBuffer;
import software.amazon.awssdk.core.sync.RequestBody;

import org.springframework.stereotype.Service;

@Service
public class S3Service {

    private S3Client s3Client;


    private String bucketName;
    public S3Service(@Value("${aws.accessKeyId}") String accessKeyId,
                     @Value("${aws.secretKey}") String secretKey,
                     @Value("${aws.s3.region}") String region,
                      @Value("${aws.s3.bucket-name}") String bucketName){
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretKey)))
                .build();
        this.bucketName = bucketName;
    }

    public PutObjectResponse uploadFile(String key, String content) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType("text/html")
                .key(key)
                .build();
        RequestBody requestBody = RequestBody.fromByteBuffer(ByteBuffer.wrap(content.getBytes()));

        return s3Client.putObject(putObjectRequest, requestBody);
    }
}
// Compare this snippet from src/main/java/com/cool/app/newssentimentanalysis/service/ChatResponseService.java: