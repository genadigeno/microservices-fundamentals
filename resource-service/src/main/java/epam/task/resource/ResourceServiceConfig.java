package epam.task.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Configuration
@EnableRetry
@EnableFeignClients
@EnableDiscoveryClient
public class ResourceServiceConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.endpoint-url}")
    private String endpointUrl;

    @Bean
    public S3Client s3Client() {
        log.info("accessKeyId: {}, endpoint: {}", accessKeyId, endpointUrl);
        log.info("s3 client initializing...");
        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpointUrl))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)// Critical for LocalStack
                                .build()
                )
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }
}
