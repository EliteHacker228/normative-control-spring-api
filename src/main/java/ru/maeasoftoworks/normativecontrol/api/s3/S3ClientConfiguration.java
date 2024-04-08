package ru.maeasoftoworks.normativecontrol.api.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class S3ClientConfiguration {
    @Value("${s3.region}")
    public String region;

    @Value("${s3.accessKeyId}")
    public String accessKeyId;

    @Value("${s3.secretAccessKey}")
    public String secretAccessKey;

    @Value("${s3.endpoint}")
    public String endpoint;

    @Bean
    public S3Client s3Client() throws URISyntaxException {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(() -> AwsBasicCredentials.create(accessKeyId, secretAccessKey))
                .endpointOverride(new URI(endpoint))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .checksumValidationEnabled(false)
                                .chunkedEncodingEnabled(true)
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }
}