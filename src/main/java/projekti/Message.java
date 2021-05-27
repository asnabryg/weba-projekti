
package projekti;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message extends AbstractPersistable<Long>{
    
    private Account sender;
    private String text;
    private LocalDate date;
    
    @OneToMany
    private List<Vote> votes;
    
    @OneToMany
    private List<Comment> comments;
}
