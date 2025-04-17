package task.epam.storageapp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/storages")
public class StorageController {
    private final static Logger log = LoggerFactory.getLogger(StorageController.class);
    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Integer>> createStorage(@Valid @RequestBody StorageDto data) {
        log.info("Creating storage data: {}", data);
        return ResponseEntity.ok(storageService.create(data));
    }

    @GetMapping
    public ResponseEntity<List<StorageDto>> getStorages() {
        log.info("Retrieving storage data...");
        return ResponseEntity.ok(storageService.get());
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Integer>>> deleteStorages(@Valid @Length(min = 1, max = 100)
                                                                     @RequestParam("id") String ids) {
        log.info("Deleting storage data: {}", ids);
        return ResponseEntity.ok(storageService.delete(ids));
    }
}
