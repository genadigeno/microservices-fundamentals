package epam.task.resource.repository;

import epam.task.resource.model.SongResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<SongResource, Integer> {
    @Query("select sr from SongResource as sr where sr.id in ?1")
    List<SongResource> findAllByIds(List<Integer> ids);
}
