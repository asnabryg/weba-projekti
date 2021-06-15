package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FileObjectRepository extends JpaRepository<FileObject, Long> {

    List<FileObject> findByOwner(Account owner);

    @Modifying
    @Query(value = "DELETE from file_object f WHERE f.id = ?1", nativeQuery = true)
    void deleteFileObjectWithId(Long id);
}
