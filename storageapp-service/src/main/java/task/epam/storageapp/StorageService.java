package task.epam.storageapp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    private final StorageRepository storageRepository;

    public Map<String, Integer> create(@Valid StorageDto data) {
        log.info("Creating storage {}", data);
        return null;
    }

    public List<StorageDto> get() {
        log.info("Getting storage list");
        return null;
    }

    public Map<String, List<Integer>> delete(String ids) {
        log.info("Deleting storage {}", ids);
        return null;
    }
}
