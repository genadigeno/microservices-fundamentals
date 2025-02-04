package epam.task.song.reqres;

import epam.task.song.util.DateTimeFormat;
import epam.task.song.util.Year;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class SongDto implements Serializable {
    @lombok.NonNull
    //@Numeric
    private String id;
    @Length(min = 1, max = 100)
    private String name;
    @Length(min = 1, max = 100)
    private String artist;
    @Length(min = 1, max = 100)
    private String album;
    @DateTimeFormat(value = "mm:ss", message = "invalid duration format")
    private String duration;
    @Year(longFormat = true)
    private String year;
}
