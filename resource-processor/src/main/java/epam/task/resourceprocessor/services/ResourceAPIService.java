package epam.task.resourceprocessor.services;

import epam.task.resourceprocessor.clients.ResourceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ResourceAPIService {
    private final RestTemplate restTemplate;
    private final ResourceClient resourceClient;

    @Value("${resource.service.url}")
    private String resourceServiceUrl;

    public ResponseEntity<byte[]> getResource(int resourceId) {
        //return restTemplate.getForEntity(resourceServiceUrl + "/resources/" + resourceId, byte[].class);
        return resourceClient.getResource(resourceId);
    }
}
