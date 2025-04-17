package epam.task.resource.service;

import epam.task.resource.clients.StorageClient;
import epam.task.resource.clients.StorageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageAPIService {
    private final StorageClient storageClient;

    public List<StorageDto> getStorages() {
        log.info("get storages from storageClient...");
        ResponseEntity<List<StorageDto>> responseEntity;
        try {
            responseEntity = storageClient.getStorages();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("error...");
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Internal Server Error");
        }
        return responseEntity.getBody();
    }
}
