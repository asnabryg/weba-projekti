
package projekti;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileObject extends AbstractPersistable<Long>{
    
    private String name;
    private String mediaType;
    private Long size;
    private String description;
    
    @ManyToOne()
    private Account owner;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;
    
}
