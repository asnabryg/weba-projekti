
package projekti;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findBySender(Account sender);
    
    @Query(value="select * from Message where sender_id IN ?1 order by id desc limit ?2 offset ?3", nativeQuery=true)
    List<Message> findBySenderIds(List<Long> senderIds, int limit, int offset);
    
    @Query(value="select M.*, V.voted from Message M, Vote V where M.sender_id IN ?1 and V.giver_id=?4 order by id desc limit ?2 offset ?3", nativeQuery=true)
    List<Message> findBySenderIdsWithVotes(List<Long> senderIds, int limit, int offset, Long accountId);
    
    @Query(value="SELECT COUNT(*) FROM Message WHERE sender_id IN ?1", nativeQuery=true)
    long countBySender(List<Long> sender_ids);
    
}
