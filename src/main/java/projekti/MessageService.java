
package projekti;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepo;
    
    public List<Message> findAll(){
        return messageRepo.findAll();
    }
    
    public List<Message> findMessagesByAccount(Account sender){
        return messageRepo.findBySender(sender);
    }
    
    public List<Message> findMessagesByAccounts(List<Long> senderIds, Long page){
        Long limit = 25L;
        Long offsetLong = (limit * page) - limit;
        int offset = offsetLong.intValue();
        return messageRepo.findBySenderIds(senderIds, 25, offset);
    }
    
    @Transactional
    public void save(Message message){
        messageRepo.save(message);
    }
    
    public long getCountBySenders(List<Long> senderIds){
        return messageRepo.countBySender(senderIds);
    }
    
}
