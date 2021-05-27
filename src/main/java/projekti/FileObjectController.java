package projekti;

import java.io.IOException;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FileObjectController {

    @Autowired
    private FileObjectService fileService;

    @Autowired
    private AccountService accountService;

    @ResponseBody
    @GetMapping("/images/{id}/content")
    public byte[] showContent(@PathVariable Long id) {
        return fileService.getFileObject(id).getContent();
    }

    @PostMapping("/addFile")
    public String addFile(@RequestParam("file") MultipartFile file, @RequestParam String type,
            @RequestParam String description) throws IOException {
        ArrayList<String> compatibles = new ArrayList();
        compatibles.add("image/jpeg");
        compatibles.add("image/jpg");
        compatibles.add("image/png");
        if (!type.equals("profileImage")) {
            compatibles.add("image/gif");
        }
        if (compatibles.contains(file.getContentType())) {
            FileObject fb = new FileObject();
            fb.setName(removeFileTypes(file.getOriginalFilename()));
            fb.setMediaType(file.getContentType());
            fb.setSize(file.getSize());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Account account = accountService.getAccount(username, false);
            fb.setOwner(account);
            fb.setDescription(description);
            fb.setContent(file.getBytes());
            fileService.addNewFileObject(fb);
            if (type.equals("profileImage")) {
                account.setProfileImage(fb);
                account.getAllProfileImages().add(fb);
                accountService.save(account);
            }
        }
        return "redirect:/profile";
    }

    @PostMapping("/changeProfileImage")
    public String changeProfileImgage(@RequestParam Long profileImageId) {
        Account account = accountService.getAccount(
                SecurityContextHolder.getContext().getAuthentication().getName(), false);
        FileObject fb = fileService.getFileObject(profileImageId);
        account.setProfileImage(fb);
        accountService.save(account);
        return "redirect:/profile";

    }
    
    private String removeFileTypes(String name){
        name = name.replace(".jpg", "");
        name = name.replace(".jpeg", "");
        name = name.replace(".png", "");
        name = name.replace(".gif", "");
        return name;
    }
}
