package epam.task.song.reqres;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ErrorMessage {
    private String errorMessage;
    private String errorCode;
    private Map<String, String> details;
}
