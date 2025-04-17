package task.epam.storageapp;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StorageService {
    private static final Logger log = LoggerFactory.getLogger(StorageService.class);
    private final StorageRepository storageRepository;

    public StorageService(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    public Map<String, Integer> create(@Valid StorageDto data) {
        log.info("Creating storage {}", data);

        Storage entity = new Storage();
        entity.setBucket(data.getBucket());
        entity.setPath(data.getPath());
        entity.setStorageType(data.getStorageType());
        Storage saved = storageRepository.save(entity);

        return Map.of("id", saved.getId());
    }

    public List<StorageDto> get() {
        log.info("Getting storage list");
        return storageRepository.findAll().stream()
                .map(storage -> {
                    StorageDto storageDto = new StorageDto();
                    storageDto.setStorageType(storage.getStorageType());
                    storageDto.setBucket(storage.getBucket());
                    storageDto.setPath(storage.getPath());
                    return storageDto;
                })
                .toList();
    }

    @Transactional(timeout = 10_000, rollbackFor = Exception.class)
    public Map<String, List<Integer>> delete(String ids) {
        log.info("Deleting storage {}", ids);
        List<Storage> resources = storageRepository.findAllByIds(toIntArray(ids));
        if (!resources.isEmpty()) {
            storageRepository.deleteAll(resources);
        }

        return Map.of("ids", resources.stream()
                .map(Storage::getId)
                .collect(Collectors.toList())
        );
    }

    private static List<Integer> toIntArray(String ids) {
        String[] nums = ids.split(",");
        //validate numeric value
        boolean allNumeric = Arrays.stream(nums).allMatch(id -> id.matches("^\\d+$"));
        if (!allNumeric) {
            throw new IllegalArgumentException("Invalid parameter \"id\" => value must be numeric");
        }

        return Arrays.stream(nums)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
