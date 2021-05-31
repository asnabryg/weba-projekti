
package projekti;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageComment extends AbstractPersistable<Long> {
    
    @ManyToOne
    private FileObject file;
    
    @ManyToOne
    private Account sender;
    
    private String text;
}
