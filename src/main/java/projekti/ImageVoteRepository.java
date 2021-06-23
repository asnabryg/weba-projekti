
package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ImageVoteRepository extends JpaRepository<ImageVote, Long> {
    
    List<ImageVote> findByFile(FileObject file);
    
    ImageVote findByFileAndGiver(FileObject file, Account giver);
    
    @Modifying
    @Query(value = "DELETE from image_vote f WHERE f.id = ?1", nativeQuery = true)
    void deleteImageVoteWithId(Long id);
}
