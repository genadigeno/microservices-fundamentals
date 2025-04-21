package epam.task.resource.process;

import epam.task.resource.clients.StorageDto;
import epam.task.resource.model.SongResource;
import epam.task.resource.service.ResourceService;
import epam.task.resource.service.StorageAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

@Component
public class ProcessorConsumer {
    private static final Logger log = LoggerFactory.getLogger(ProcessorConsumer.class);

    private final StorageAPIService storageAPIService;
    private final ResourceService resourceService;
    private final S3Client s3Client;

    public ProcessorConsumer(ResourceService resourceService, S3Client s3Client, StorageAPIService storageAPIService){
        this.storageAPIService = storageAPIService;
        this.resourceService = resourceService;
        this.s3Client = s3Client;
    }

    @RabbitListener(queues = "file-completion.queue")
    public void process(Message message) {
        try {
            log.info("Message received: {}", message.getBody());
            if ("created".equals(new String(message.getBody()))) {
                log.info("extracting resource id...");
                int resourceId = extractResourceId(message.getMessageProperties().getHeader("resourceId"));
                log.info("calling resource service with id {}", resourceId);
                SongResource resource = resourceService.getResource(resourceId);

                log.info("retrieving storage data from storage service...");
                List<StorageDto> storages = storageAPIService.getStorages();

                log.info("retrieving staging storage info...");
                StorageDto stagingStorage = storages.stream()
                        .filter(storageDto -> storageDto.getStorageType().equalsIgnoreCase("staging"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("staging storage not found"));

                log.info("retrieving object from bucket: {}", stagingStorage.getBucket());
                ResponseBytes<GetObjectResponse> object = s3Client.getObjectAsBytes(
                        GetObjectRequest.builder()
                                .bucket(stagingStorage.getBucket())
                                .key(resource.getLocation())
                                .build()
                );

                log.info("retrieving permanent storage info...");
                StorageDto permanentStorage = storages.stream()
                        .filter(storageDto -> storageDto.getStorageType().equalsIgnoreCase("permanent"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("permanent storage not found"));

                log.info("sending object to bucket {}", permanentStorage.getBucket());
                PutObjectRequest objectRequest = PutObjectRequest.builder()
                        .bucket(permanentStorage.getBucket())
                        .key(resource.getLocation())// files/fileName
                        .contentType(SongResource.RESOURCE_CONTENT_TYPE)
                        .build();
                PutObjectResponse putResponse =
                        s3Client.putObject(objectRequest, RequestBody.fromBytes(object.asByteArray()));

                log.info("deleting object from bucket {}", stagingStorage.getBucket());
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(stagingStorage.getBucket())
                        .key(resource.getLocation())
                        .build();
                DeleteObjectResponse deleteResponse = s3Client.deleteObject(deleteObjectRequest);

                log.info("updating file state from STAGING to PERMANENT");
                resource.setFileState("PERMANENT");
                resource.setBucketName(permanentStorage.getBucket());
                resourceService.save(resource);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static int extractResourceId(Object resourceId) {
        if (resourceId instanceof String) {
            return Integer.parseInt((String) resourceId);
        }
        else if (resourceId instanceof Integer) {
            return (Integer) resourceId;
        }
        throw new IllegalArgumentException("ResourceId must be an integer");
    }

}
