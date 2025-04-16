package task.epam.storageapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageDto implements Serializable {
    private String storageType;
    private String bucket;
    private String path;
}
