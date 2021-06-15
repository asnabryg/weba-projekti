package projekti;

import java.io.IOException;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    
    @ResponseBody
    @GetMapping("/profileImage/{username}")
    public byte[] showProfileImageContent(@PathVariable String username) {
        Account acc = accountService.getAccount(username, false);
        return acc.getProfileImage().getContent();
    }

    @PostMapping("/addFile")
    public String addFile(@RequestParam("file") MultipartFile file, @RequestParam(required = false) String imageTo,
            @RequestParam String description) throws IOException {
        String username = getUsername();
        Account account = accountService.getAccount(username, false);
        if (account.getImages().size() >= 10) {
            return "redirect:/profile/" + username + "/1" + "?error=max";
        }
        ArrayList<String> compatibles = new ArrayList();
        compatibles.add("image/jpeg");
        compatibles.add("image/jpg");
        compatibles.add("image/png");
        if (imageTo != null && !imageTo.equals("profile")) {
            compatibles.add("image/gif");
        }
        if (compatibles.contains(file.getContentType())) {
            FileObject fo = new FileObject();
            fo.setName(removeFileTypes(file.getOriginalFilename()));
            fo.setMediaType(file.getContentType());
            fo.setSize(file.getSize());
            fo.setOwner(account);
            fo.setDescription(description);
            fo.setContent(file.getBytes());
            fileService.addNewFileObject(fo);
            if (imageTo != null && imageTo.equals("profile")) {
                account.setProfileImage(fo);
            }
            account.getImages().add(fo);
            accountService.save(account);
        }else{
            return "redirect:/profile/" + username + "/1" + "?error=notCompatible";
        }
        return "redirect:/profile";
    }

    @PostMapping("/changeProfileImage")
    public String changeProfileImgage(@RequestParam Long profileImageId,
            @RequestParam(required = false) String webPage) {
        if (!checkIfLoggedIn()) {
            return "redirect:/login";
        }
        Account account = accountService.getAccount(
                SecurityContextHolder.getContext().getAuthentication().getName(), false);
        FileObject fb = fileService.getFileObject(profileImageId);
        account.setProfileImage(fb);
        accountService.save(account);
        if (webPage != null) {
            return "redirect:" + webPage;
        }
        return "redirect:/profile";
    }

    @PostMapping("/showImage")
    public String showImage(@RequestParam Long imageId, @RequestParam String username) {
        return "redirect:/profile/" + username + "/image/" + imageId;
    }
    
    @PostMapping("/deleteImage")
    public String deleteImage(@RequestParam Long imageId, @RequestParam String webPage){
        FileObject fo = fileService.getFileObject(imageId);
        Account account = accountService.getAccount(getUsername(), false);
        account.getImages().remove(fo);
        if (account.getProfileImage() == fo) {
            account.setProfileImage(null);
        }
        fileService.deleteFileObject(imageId);
        accountService.save(account);
        return "redirect:" + webPage;
    }

    private String removeFileTypes(String name) {
        name = name.replace(".jpg", "");
        name = name.replace(".jpeg", "");
        name = name.replace(".png", "");
        name = name.replace(".gif", "");
        return name;
    }

    @PostMapping("/giveImageVote")
    public String giveImageVote(@RequestParam Long fileId, @RequestParam String webPage) {
        if (!checkIfLoggedIn()) {
            return "redirect:/login";
        }
        Account account = accountService.getAccount(getUsername(), false);
        FileObject file = fileService.getFileObject(fileId);

        accountService.voteImage(account, file);
        return "redirect:" + webPage;
    }

    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private boolean checkIfLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getPrincipal() instanceof UserDetails;
    }
}
