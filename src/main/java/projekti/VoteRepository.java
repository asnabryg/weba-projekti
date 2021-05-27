
package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    List<Vote> findByMessage(Message message);
    
    Vote findByMessageAndGiver(Message message, Account giver);
}
