package task.epam.storageapp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StorageService {
    private static final Logger log = LoggerFactory.getLogger(StorageService.class);
    private final StorageRepository storageRepository;

    public StorageService(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    public Map<String, Integer> create(@Valid StorageDto data) {
        log.info("Creating storage {}", data);
        return null;
    }

    public List<StorageDto> get() {
        log.info("Getting storage list");
        List<StorageDto> list = storageRepository.findAll().stream()
                .map(storage -> {
                    StorageDto storageDto = new StorageDto();
                    storageDto.setStorageType(storage.getStorageType());
                    storageDto.setBucket(storage.getBucket());
                    storageDto.setPath(storage.getPath());
                    return storageDto;
                })
                .toList();
        return list;
    }

    public Map<String, List<Integer>> delete(String ids) {
        log.info("Deleting storage {}", ids);
        return null;
    }
}
