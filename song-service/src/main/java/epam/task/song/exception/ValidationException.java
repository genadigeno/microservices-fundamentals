package epam.task.song.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class ValidationException extends RuntimeException {
    protected Map<?, ?> details;

    public ValidationException(String message, Map<?, ?> details) {
        super(message);
        this.details = details;
    }
}
