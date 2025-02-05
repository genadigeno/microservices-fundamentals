package epam.task.song.reqres;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
public class ErrorMessageDetails extends ErrorMessage {
    private Map<String, String> details;
}
