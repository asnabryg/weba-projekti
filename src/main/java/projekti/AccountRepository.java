
package projekti;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    
    // List<Account> findSomething(jotai);
    
    Account findByUsername(String username);
    Account findByNickname(String nickname);
}
