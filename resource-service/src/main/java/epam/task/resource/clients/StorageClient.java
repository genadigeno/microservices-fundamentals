package epam.task.resource.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "${storage.service.name}")
public interface StorageClient {
    @GetMapping("/storages")
    ResponseEntity<List<StorageDto>> getStorages();
}
