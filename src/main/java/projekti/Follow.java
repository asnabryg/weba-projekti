package projekti;

import java.time.LocalDate;
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
public class Follow extends AbstractPersistable<Long> {
    
    @ManyToOne
    private Account follower;
    
    @ManyToOne
    private Account following;
    
    // -3: both blocked, -2: other blocked, -1: you blocked, 0: no follow, 1: following
    private Integer status;
    
    private LocalDate date;
}
