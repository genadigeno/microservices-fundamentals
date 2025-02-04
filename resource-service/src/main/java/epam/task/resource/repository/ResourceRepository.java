package epam.task.resource.repository;

import epam.task.resource.model.SongResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<SongResource, Integer> {
    //get only the ids that exist in db to return entity's id who was deleted
    @Query("select sr.id from SongResource as sr where sr.id in ?1")
    List<Integer> getAllIdaByIds(List<Integer> ids);
}
