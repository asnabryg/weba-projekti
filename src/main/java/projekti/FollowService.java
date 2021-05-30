
package projekti;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowService {
    
    @Autowired
    private FollowRepository followRepo;
    
    public List<Follow> findAll(){
        return followRepo.findAll();
    }
    
    public List<Follow> findAllFollowings(Account account){
        return followRepo.findByFollower(account);
    }
    
    public List<Follow> findAllFollowers(Account account){
        return followRepo.findByFollowing(account);
    }
    
    public List<Follow> findAllByFollowingAndStatus(Account account, List<Integer> statuses){
        return followRepo.findByFollowingAndStatus(account.getId(), statuses);
    }
    
    public List<Follow> findAllByFollowerAndStatus(Account account, List<Integer> statuses){
        return followRepo.findByFollowerAndStatus(account.getId(), statuses);
    }
    public Follow findByFollowerAndFollowing(Account follower, Account following){
        return followRepo.findByFollowerAndFollowing(follower, following);
    }
    
    @Transactional
    public void save(Follow f){
        followRepo.save(f);
    }
    
}
