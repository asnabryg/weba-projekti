
package projekti;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractPersistable<Long>{
    
    private String username;
    private String password;
    private String nickname;
    
    @OneToMany(mappedBy="sender")
    private List<Message> messages;
    
    @OneToMany(mappedBy="giver")
    private List<Vote> votes;
    
    @OneToMany(mappedBy="comments")
    private List<Comment> comments;
    
    private FileObject profileImage;
    
    @OneToMany(mappedBy="owner")
    private List<FileObject> images;
    
    private List<Account> following;   
    private List<Account> followers;
    private List<Account> blockedAccounts;
}
