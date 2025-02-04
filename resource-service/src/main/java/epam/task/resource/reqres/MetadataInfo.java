package epam.task.resource.reqres;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetadataInfo {
    private String id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
}
