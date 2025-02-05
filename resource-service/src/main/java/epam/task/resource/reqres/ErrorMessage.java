package epam.task.resource.reqres;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@Builder
public class ErrorMessage {
    private String errorMessage;
    private String errorCode;
}
