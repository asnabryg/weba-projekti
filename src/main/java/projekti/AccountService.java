package projekti;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;
    
    @Autowired
    private MessageRepository messageRepo;
    
    @Autowired
    private VoteService voteService;

    @Transactional
    public void save(Account account) {
        accountRepo.save(account);
    }

    public List<Account> findAll() {
        return accountRepo.findAll();
    }

    public Account getAccount(Long id) {
        return accountRepo.getOne(id);
    }

    public Account getAccount(String name, boolean nickname) {
        if (!nickname) {
            return accountRepo.findByUsername(name);
        } else {
            return accountRepo.findByNickname(name);
        }
    }
    
    public List<Account> findAllLikeUsername(String username){
        return accountRepo.findAllByUsernameContainingIgnoreCase(username);
    }
    
    public List<Account> findAllLikeNickname(String nickaname){
        return accountRepo.findAllByNicknameContainingIgnoreCase(nickaname);
    }
    
    public List<Account> getFiveRandomAccounts(String username){
        return accountRepo.getFiveRandomAccounts(username);
    }
    
    @Transactional
    public void vote(Account account, Message message){
        Vote vote = voteService.findByMessageAndGiver(message, account);
        if (vote == null) {
            vote = new Vote(message, account, false);
        }
        vote.setVoted(!vote.isVoted());
        voteService.saveVote(vote);
    }
    
    @Transactional
    public void voteImage(Account account, FileObject file){
        ImageVote vote = voteService.findByFileAndGiver(file, account);
        if (vote == null) {
            vote = new ImageVote(file, account, false);
        }
        vote.setVoted(!vote.isVoted());
        voteService.saveImageVote(vote);
    }
}
