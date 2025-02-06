package epam.task.song.reqres;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
public class DetailedErrorMessage extends ErrorMessage {
    private Map<?, ?> details;
}
