package task.epam.storageapp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/storages")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

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
