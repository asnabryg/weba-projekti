
package projekti;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
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
    
    @OneToMany(mappedBy="giver")
    private List<ImageVote> imageVotes;
    
    @OneToMany(mappedBy="sender")
    private List<Comment> comments;
    
    @OneToOne
    private FileObject profileImage;
    
    @OneToMany
    private List<FileObject> images;
    
    @OneToMany(mappedBy="follower")
    private List<Follow> follows;
    
}
