
package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
    
    List<FileObject> findByOwner(Account owner);
}
