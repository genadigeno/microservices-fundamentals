package epam.task.song.repository;

import epam.task.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    //get only the ids that exist in db to return entity's id who was deleted
    @Query("select s.id from Song as s where s.id in :ids")
    List<Integer> getAllIdsByIds(@Param("ids") List<Integer> ids);

    boolean existsById(int id);
}
