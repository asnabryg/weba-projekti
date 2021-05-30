
package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageVoteRepository extends JpaRepository<ImageVote, Long> {
    
    List<ImageVote> findByFile(FileObject file);
    
    ImageVote findByFileAndGiver(FileObject file, Account giver);
}
