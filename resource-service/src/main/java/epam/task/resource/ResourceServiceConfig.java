package epam.task.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
@EnableRetry
public class ResourceServiceConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Bean
    public S3Client s3Client() {
        log.info("accessKeyId: {}", accessKeyId);
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }
}
