
package projekti;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote extends AbstractPersistable<Long> {
    
    @ManyToOne()
    private Message message;
    
    @ManyToOne()
    private Account giver;
    
    private boolean voted = false;
}
