package epam.task.resource.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "song_resources", schema = "public")
@Getter
@Setter
public class SongResource {
    public final static String RESOURCE_CONTENT_TYPE = "audio/mpeg";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_resources_sequence")
    @SequenceGenerator(name = "song_resources_sequence", sequenceName = "song_resources_sequence", allocationSize = 1)
    private int id;

    private String location;
}
