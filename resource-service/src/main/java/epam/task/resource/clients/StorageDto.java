package epam.task.resource.clients;

import lombok.Data;

@Data
public class StorageDto {
    private String storageType;
    private String bucket;
    private String path;
}
