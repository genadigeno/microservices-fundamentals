package epam.task.song.reqres;

import epam.task.song.util.DateTimeFormat;
import epam.task.song.util.Year;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class SongDto implements Serializable {

    @NotEmpty
    private String id;
    @Length(min = 1, max = 100)
    @NotEmpty
    private String name;
    @Length(min = 1, max = 100)
    @NotEmpty
    private String artist;
    @Length(min = 1, max = 100)
    @NotEmpty
    private String album;
    @DateTimeFormat(value = "mm:ss", message = "invalid duration format")
    @NotEmpty
    private String duration;
    @NotEmpty
    @Year(longFormat = true)
    private String year;
}
