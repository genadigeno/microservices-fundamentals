package epam.task.resource.reqres;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ErrorMessage {
    private String errorMessage;
    private String errorCode;
}
