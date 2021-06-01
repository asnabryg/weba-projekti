package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // List<Account> findSomething(jotai);
    Account findByUsername(String username);

    Account findByNickname(String nickname);

    List<Account> findAllByUsernameContainingIgnoreCase(String username);

    List<Account> findAllByNicknameContainingIgnoreCase(String nickname);

    @Query(value = "SELECT * FROM Account WHERE username != ?1 ORDER BY RANDOM() LIMIT 5",
            nativeQuery = true)
    List<Account> getFiveRandomAccounts(String username);
}
