package epam.task.resourceprocessor.services;

import epam.task.resourceprocessor.reqres.MetadataInfo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SongAPIService {
    private final RestTemplate restTemplate;

    @Value("${song.service.url}")
    private String songServiceUrl;

    public SongAPIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<Map<String, Integer>> createSong(MetadataInfo requestBody) {
        return restTemplate.postForEntity(songServiceUrl + "/songs", requestBody,
                (Class<Map<String, Integer>>) (Class<?>) Map.class);
    }
}
