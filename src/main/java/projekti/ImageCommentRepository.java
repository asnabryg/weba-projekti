
package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ImageCommentRepository extends JpaRepository<ImageComment, Long> {
    
    List<Comment> findBySender(Account sender);
    
    @Modifying
    @Query(value = "DELETE from image_comment f WHERE f.id = ?1", nativeQuery = true)
    void deleteImageCommentWithId(Long id);
}
