package epam.task.resourceprocessor.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ResourceAPIService {
    private final RestTemplate restTemplate;

    @Value("${resource.service.url}")
    private String resourceServiceUrl;

    public ResourceAPIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<byte[]> getResource(int resourceId) {
        return restTemplate.getForEntity(resourceServiceUrl + "/resources/" + resourceId, byte[].class);
    }
}
