package task.epam.storageapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Integer> {
    @Query("select st from Storage as st where st.id in ?1")
    List<Storage> findAllByIds(List<Integer> ids);
}
