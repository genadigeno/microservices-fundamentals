package epam.task.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
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
@EnableCaching
public class ResourceServiceConfig {

    /* - - - - - - - - - - - - AWS S3 config - - - - - - - - - - - - */

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
                .forcePathStyle(true)// Critical for LocalStack
                /*.serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )*/
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }

    /* - - - - - - - - - - - - Rabbit MQ config - - - - - - - - - - - - */

    @Bean
    public Queue queue() {
        return new Queue("file-completion.queue", false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("file-completion.exchange");
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("*");
    }

    /* - - - - - - - - - - - - Caching - - - - - - - - - - - - */

    @Bean
    public CacheManager cacheManager() {
        return new CaffeineCacheManager();
    }
}
