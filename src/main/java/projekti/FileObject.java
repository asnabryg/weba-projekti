
package projekti;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
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
    
    @OneToMany(mappedBy="file")
    private List<ImageVote> imageVotes;
    
    @OneToMany(mappedBy="file")
    private List<ImageComment> comments;
    
    
//    @Lob
//    @Basic(fetch = FetchType.LAZY)
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] content;
    
}
