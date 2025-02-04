package epam.task.song.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "songs_metadata", schema = "public")
@Getter
@Setter
public class Song {
    @Id
    private int id;//not auto increasing for it, coz of the resource service provides id.
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
}
