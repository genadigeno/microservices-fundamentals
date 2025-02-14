package epam.task.resource.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExternalServiceException extends RuntimeException {
    private final int status;
    private final String message;
}
