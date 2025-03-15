package epam.task.resource.service;

import epam.task.resource.exception.FileFormatException;
import epam.task.resource.exception.IllegalParameterException;
import epam.task.resource.model.SongResource;
import epam.task.resource.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    private final Tika tika = new Tika();

    private final ResourceRepository resourceRepository;
    private final S3Client s3Client;
    private final RabbitTemplate rabbitTemplate;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Transactional(timeout = 10_000, rollbackFor = Exception.class)
    public Map<String, Integer> create(HttpServletRequest request) throws IOException {

        byte[] fileBytes = StreamUtils.copyToByteArray(request.getInputStream());
        if (!SongResource.RESOURCE_CONTENT_TYPE.equals(tika.detect(fileBytes))) {
            throw new FileFormatException("uploaded file's format is not mp3");
        }

        String fileName = UUID.randomUUID() +".mp3";

        //store into DB
        SongResource resource = new SongResource();
        resource.setLocation(fileName);

        logger.info("Creating resource...");
        final SongResource saved = resourceRepository.save(resource);

        //store into AWS S3
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(SongResource.RESOURCE_CONTENT_TYPE)
                .build();

        logger.info("sending object to bucket {}", bucketName);
        s3Client.putObject(objectRequest, RequestBody.fromBytes(fileBytes));

        //send a message for resource processor
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> sendMessage(saved.getId()));

        return Map.of("id", saved.getId());
    }

    //test whether retry mechanism works well.
    private int retryTestCounter = 0;

    @Retryable(maxAttempts = 2, retryFor = AmqpException.class)
    public void sendMessage(int resourceId) {
        logger.info("retry test counter {}", retryTestCounter);

        /*if (retryTestCounter < 1) {
            retryTestCounter++;
            throw new AmqpException("manual fail");
        }*/

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeaders(Map.of("resourceId", resourceId));
        Message message = new Message("created".getBytes(StandardCharsets.UTF_8), messageProperties);
        rabbitTemplate.send("resources.exchange","*", message);

        logger.info("resetting retry test counter...");
        retryTestCounter = 0;
    }

    public byte[] get(int id) {
        logger.info("Retrieving resource...");
        SongResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("resource with ID="+id+" not found"));

        logger.info("Retrieving a resource from aws s3 bucket[{}]...", bucketName);
        ResponseBytes<GetObjectResponse> object = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(resource.getLocation())
                        .build()
        );

        logger.info("Retrieved resource from aws s3 bucket[{}], size of a file: {}",
                bucketName, object.asByteArray().length);
        return object.asByteArray();
    }

    @Transactional(timeout = 10_000, rollbackFor = Exception.class)
    public Map<String, List<Integer>> delete(String ids) {
        ///get the ids that exist in resource DB
        List<SongResource> resources = resourceRepository.findAllByIds(toIntArray(ids));

        if (!resources.isEmpty()) {
            ///1. delete from aws S3 bucket. if it fails we will throw an exception that rolls back our changes.
            resources.forEach(resource -> {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(resource.getLocation())
                        .build());
            });

            ///2. then delete from db
            logger.info("Deleting resource(s)...");
            resourceRepository.deleteAll(resources);
        }

        return Map.of("ids", resources.stream()
                .map(SongResource::getId)
                .collect(Collectors.toList())
        );
    }

    private static List<Integer> toIntArray(String ids) {
        String[] nums = ids.split(",");
        //validate numeric value
        boolean allNumeric = Arrays.stream(nums).allMatch(id -> id.matches("^\\d+$"));
        if (!allNumeric) {
            throw new IllegalParameterException("Invalid parameter", Map.of("id", "value must be numeric"));
        }

        return Arrays.stream(nums)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
