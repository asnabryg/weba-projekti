
package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageCommentRepository extends JpaRepository<ImageComment, Long> {
    
    List<Comment> findBySender(Account sender);
}
