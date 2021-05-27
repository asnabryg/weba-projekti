package projekti;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

    @Transactional
    public void addNewAccount(Account account) {
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
}
