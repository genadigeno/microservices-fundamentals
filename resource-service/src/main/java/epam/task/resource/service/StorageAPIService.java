package epam.task.resource.service;

import epam.task.resource.clients.StorageClient;
import epam.task.resource.clients.StorageDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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

    @CircuitBreaker(name = "CircuitBreakerService", fallbackMethod = "fallbackAfterRetry")
    public List<StorageDto> getStorages() {
        log.info("get storages from storageClient...");
        ResponseEntity<List<StorageDto>> responseEntity;
        try {
            responseEntity = storageClient.getStorages();
        } catch (Exception e) {
            log.error("service unavailable", e);
            throw new RuntimeException("service unavailable");
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Error: {}", responseEntity.getBody());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Internal Server Error");
        }
        return responseEntity.getBody();
    }

    public List<StorageDto> fallbackAfterRetry(Exception ex) {
        log.warn("invoking fallbackAfterRetry method due to: {}", ex.getMessage());
        StorageDto stagingStorageDto = new StorageDto();
        stagingStorageDto.setBucket("staging");
        stagingStorageDto.setPath("files");
        stagingStorageDto.setStorageType("STAGING");

        StorageDto permanentStorageDto = new StorageDto();
        permanentStorageDto.setBucket("permanent");
        permanentStorageDto.setPath("files");
        permanentStorageDto.setStorageType("PERMANENT");

        return List.of(stagingStorageDto, permanentStorageDto);
    }
}
