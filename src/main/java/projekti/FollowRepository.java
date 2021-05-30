package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> findByFollower(Account follower);

    List<Follow> findByFollowing(Account following);

    Follow findByFollowerAndFollowing(Account follower, Account following);

    @Query(value="SELECT * FROM Follow WHERE follower_id = ?1 AND status IN ?2", nativeQuery = true)
    List<Follow> findByFollowerAndStatus(Long FollowerId, List<Integer> statuses);
    
    @Query(value="SELECT * FROM Follow WHERE following_id = ?1 AND status IN ?2", nativeQuery = true)
    List<Follow> findByFollowingAndStatus(Long FollowingId, List<Integer> statuses);

}
