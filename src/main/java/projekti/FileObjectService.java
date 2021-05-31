package projekti;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileObjectService {

    @Autowired
    private FileObjectRepository fileRepo;
    

    @Transactional
    public void addNewFileObject(FileObject file) {
        fileRepo.save(file);
    }

    public List<FileObject> findAll() {
        return fileRepo.findAll();
    }

    public FileObject getFileObject(Long id) {
        return fileRepo.getOne(id);
    }

    public List<FileObject> getAccountImages(Account owner){
        return fileRepo.findByOwner(owner);
    }
}
