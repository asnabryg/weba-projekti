package projekti;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteService {

   @Autowired
   private VoteRepository voteRepo;
   
   @Autowired
   private ImageVoteRepository imageVoteRepo;
   
   public List<Vote> findAllVotes(){
       return voteRepo.findAll();
   }
   
   public List<ImageVote> findAllImageVotes(){
       return imageVoteRepo.findAll();
   }
   
   public Vote findByMessageAndGiver(Message message, Account account){
       return voteRepo.findByMessageAndGiver(message, account);
   }
   
   public ImageVote findByFileAndGiver(FileObject file, Account account){
       return imageVoteRepo.findByFileAndGiver(file, account);
   }
   
   @Transactional
   public void saveVote(Vote vote){
       voteRepo.save(vote);
   }
   
   @Transactional
   public void saveImageVote(ImageVote vote){
       imageVoteRepo.save(vote);
   }
}
