package task.epam.storageapp;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class StorageDto implements Serializable {
    public StorageDto() {
    }

    @NotNull
    private String storageType;
    @NotNull
    private String bucket;
    @NotNull
    private String path;

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
