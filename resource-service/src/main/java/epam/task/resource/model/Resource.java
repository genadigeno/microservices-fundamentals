package epam.task.resource.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "resources")
@Getter
@Setter
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resources_sequence")
    @SequenceGenerator(name = "resources_sequence", sequenceName = "resources_sequence", allocationSize = 1)
    private Integer id;

    private String name;
}
