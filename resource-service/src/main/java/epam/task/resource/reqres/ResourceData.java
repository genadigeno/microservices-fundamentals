package epam.task.resource.reqres;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceData {
    byte[] data;
}
