package epam.task.song.exception;

import java.util.Map;

public class IllegalParameterException extends ValidationException {
    public IllegalParameterException(String message, Map<?, ?> details) {
        super(message, details);
    }
}
