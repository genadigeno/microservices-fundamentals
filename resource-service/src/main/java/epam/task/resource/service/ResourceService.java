package epam.task.resource.service;

import epam.task.resource.clients.StorageDto;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    private final Tika tika = new Tika();

    private final ResourceRepository resourceRepository;
    private final S3Client s3Client;
    private final MessageService messageService;
    private final StorageAPIService storageAPIService;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Transactional(timeout = 10_000, rollbackFor = Exception.class, noRollbackFor = AmqpException.class)
    public Map<String, Integer> create(HttpServletRequest request) throws IOException {

        byte[] fileBytes = StreamUtils.copyToByteArray(request.getInputStream());
        if (!SongResource.RESOURCE_CONTENT_TYPE.equals(tika.detect(fileBytes))) {
            throw new FileFormatException("uploaded file's format is not mp3");
        }

        List<StorageDto> storages = storageAPIService.getStorages();

        StorageDto storageData = storages.stream()
                .filter(storageDto -> storageDto.getStorageType().equalsIgnoreCase("staging"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("staging storage not found"));

        String fileName = createFileName(storageData.getPath());

        //1.store into DB
        SongResource resource = new SongResource();
        resource.setLocation(fileName);
        resource.setFileState(storageData.getStorageType().toUpperCase());
        resource.setBucketName(storageData.getBucket());

        logger.info("Creating resource...");
        final SongResource saved = resourceRepository.save(resource);

        //2.store into AWS S3
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(storageData.getBucket())
                .key(fileName)// files/file-name.mp3
                .contentType(SongResource.RESOURCE_CONTENT_TYPE)
                .build();

        logger.info("sending object to bucket {} with key {}", storageData.getBucket(), fileName);
        s3Client.putObject(objectRequest, RequestBody.fromBytes(fileBytes));

        //3.send a message for resource processor
        messageService.sendMessage(saved.getId());

        return Map.of("id", saved.getId());
    }

    // returns dir/file.ext not /dir/file.ext
    private static String createFileName(String path) {
        if (path == null || path.isEmpty()) {
            return UUID.randomUUID() + ".mp3";
        }
        else {
            path = path.startsWith("/") ? path.substring(1)          : path;
            path = path.endsWith("/")   ? path.substring(0, path.length() - 1) : path;
            return path + "/" + UUID.randomUUID() + ".mp3";
        }
    }

    public byte[] get(int id) {
        logger.info("Retrieving resource...");
        SongResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("resource with ID="+id+" not found"));

        logger.info("Retrieving a resource from aws s3 bucket: {} and with key= {} ",
                resource.getBucketName(), resource.getLocation());
        ResponseBytes<GetObjectResponse> object = null;
        try {
            object = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(resource.getBucketName())
                            .key(resource.getLocation())
                            .build()
            );
        } catch (AwsServiceException | SdkClientException e) {
            logger.error("error raised while retrieving an object", e);
            throw new RuntimeException(e);
        }

        logger.info("Retrieved resource from aws s3 bucket[{}], size of a file: {}",
                resource.getBucketName(), object.asByteArray().length);
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
                        .bucket(resource.getBucketName())
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

    public SongResource getResource(int resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("resource with ID="+resourceId+" not found"));
    }

    public void save(SongResource resource) {
        resourceRepository.save(resource);
    }
}
