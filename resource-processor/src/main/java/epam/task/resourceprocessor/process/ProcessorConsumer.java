package epam.task.resourceprocessor.process;

import epam.task.resourceprocessor.reqres.MetadataInfo;
import epam.task.resourceprocessor.utils.FormatUtils;
import epam.task.resourceprocessor.utils.ResourceParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ProcessorConsumer {
    private static final Logger log = LoggerFactory.getLogger(ProcessorConsumer.class);
    private final RestTemplate restTemplate;
    private final ResourceParserService resourceParserService;

    @Value("${song.service.url}")
    private String songServiceUrl;

    @Value("${resource.service.url}")
    private String resourceServiceUrl;

    public ProcessorConsumer(RestTemplate restTemplate, ResourceParserService resourceParserService) {
        this.restTemplate = restTemplate;
        this.resourceParserService = resourceParserService;
    }

    @RabbitListener(queues = "resources.queue")
    public void process(Message message) {
        try {
            log.info("Message received: {}", message.getBody());
            if ("created".equals(new String(message.getBody()))) {
                log.info("extracting resource id...");
                int resourceId = extractResourceId(message.getMessageProperties().getHeader("resourceId"));
                log.info("calling resource service with id {}", resourceId);
                ResponseEntity<byte[]> response =
                        restTemplate.getForEntity(resourceServiceUrl + "/resources/" + resourceId, byte[].class);
                byte[] bytes = response.getBody();

                log.info("extracting metadata info...");
                Map<String, String> metadata =
                        resourceParserService.extractMetadata(new CustomMultipartFile("song", bytes));

                log.info("calling song service...");
                ResponseEntity<Map<String, Integer>> responseEntity =
                        restTemplate.postForEntity(songServiceUrl + "/songs", buildRequestBody(resourceId, metadata),
                                (Class<Map<String, Integer>>) (Class<?>) Map.class);

                if(responseEntity.getStatusCode().value() != 200) {
                    log.warn("response status is {}", responseEntity.getStatusCode().value());
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static MetadataInfo buildRequestBody(int resourceId, Map<String, String> metadata) {
        return MetadataInfo.builder()
                .id(String.valueOf(resourceId))
                .name(metadata.get("name"))
                .artist(metadata.get("artist"))
                .album(metadata.get("album"))
                .duration(FormatUtils.formatDuration(Double.parseDouble(metadata.get("duration"))))
                .year(metadata.get("year"))
                .build();
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
