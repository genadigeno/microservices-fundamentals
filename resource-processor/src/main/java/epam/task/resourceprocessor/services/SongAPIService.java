package epam.task.resourceprocessor.services;

import epam.task.resourceprocessor.clients.SongClient;
import epam.task.resourceprocessor.reqres.MetadataInfo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SongAPIService {
    private final RestTemplate restTemplate;
    private final SongClient songClient;

    @Value("${song.service.url}")
    private String songServiceUrl;

    public ResponseEntity<Map<String, Integer>> createSong(MetadataInfo requestBody) {
        /*return restTemplate.postForEntity(songServiceUrl + "/songs", requestBody,
                (Class<Map<String, Integer>>) (Class<?>) Map.class);*/

        return (ResponseEntity<Map<String, Integer>>) songClient.saveSong(requestBody);
    }
}
