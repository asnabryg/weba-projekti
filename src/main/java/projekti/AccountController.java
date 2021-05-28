package projekti;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSession session;

    @GetMapping("profile/{username}")
    public String profile(Model model, @PathVariable String username) {
        model.addAttribute("sameUser", isSameUser(username));
        model.addAttribute("logged", checkIfLoggedIn());
        if (!isSameUser(username)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            model.addAttribute("looker", accountService.getAccount(auth.getName(), false));
        }
        Account account = accountService.getAccount(username, false);
        model.addAttribute("username", username);
        model.addAttribute("nickname", account.getNickname());
        FileObject profileImage = null;
        if (account.getProfileImage() != null) {
            profileImage = account.getProfileImage();
        }
//        System.out.println("image " + profileImage.getName());
        model.addAttribute("profileImage", profileImage);
        List<FileObject> images = account.getImages();
        List<FileObject> allProfileImages = account.getAllProfileImages();
        allProfileImages.remove(profileImage);
        model.addAttribute("allProfileImages", allProfileImages);
        List<Long> imageIds = new ArrayList();
        for (FileObject image : images) {
            imageIds.add(image.getId());
        }
        model.addAttribute("imageIds", imageIds);
        model.addAttribute("following", account.getFollowing());
        model.addAttribute("followers", account.getFollowers());
        model.addAttribute("blockedUsers", account.getBlockedAccounts());
        return "profile";
    }
    
    @PostMapping("/follow/{username}")
    public String followUser(@PathVariable String username){
        if (checkIfLoggedIn()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println(auth.getName() + " " + username);
            accountService.setFollow(auth.getName(), username);
        }
        return "redirect:/profile/" + username;
    }
    
    @PostMapping("/unfollow/{username}")
    public String unfollowUser(@PathVariable String username){
        if (checkIfLoggedIn()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            accountService.unfollow(auth.getName(), username);
        }
        return "redirect:/profile/" + username;
    }
    
    @PostMapping("/changeNickname")
    public String newNickname(@RequestParam String newNickname){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account account = accountService.getAccount(username, false);
        account.setNickname(newNickname);
        accountService.save(account);
        return "redirect:/profile/" + username;
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam Map<String, String> params) {
        if (checkIfLoggedIn()) {
            if (params.containsKey("logout")) {
                session.setAttribute("logged", false);
                return "login";
            }
            return "redirect:/index";
        }
        if (params.containsKey("error")) {
            model.addAttribute("error", params.get("error").equals("true"));
        }
        return "login";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess() {
        if (checkIfLoggedIn()) {
            session.setAttribute("logged", true);
        }
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (checkIfLoggedIn()) {
            return "redirect:/index";
        }
        model.addAttribute("accounts", accountService.findAll());
        if (session.getAttribute("success") == null) {
            session.setAttribute("success", 0);
        }
        model.addAttribute("success", session.getAttribute("success"));
        session.setAttribute("success", 0);
        return "register";
    }

    @PostMapping("register")
    public String postRegister(@RequestParam String username, @RequestParam String password,
            @RequestParam String nickname, @RequestParam String passwordConfirm) {
        if (checkIfLoggedIn()) {
            return "redirect:/index";
        }
        if (accountService.getAccount(username, false) != null) {
            session.setAttribute("success", "Username is already used!");
            return "redirect:/register";
        }
        System.out.println("Nickname: " + nickname);
        if (!password.equals(passwordConfirm)) {
            session.setAttribute("success", "Passwords don't match!");
            return "redirect:/register";
        }
        if (username.equals("") || nickname.equals("") || password.equals("") || passwordConfirm.equals("")) {
            session.setAttribute("success", "Fill all fields!");
            return "redirect:/register";
        }
        Account account = new Account();
        account.setUsername(username);
        account.setNickname(nickname);
        account.setPassword(passwordEncoder.encode(password));
        accountService.save(account);
        session.setAttribute("success", 1);
        return "redirect:/register";
    }

    @GetMapping("/clear")
    public String clear() {
        session.invalidate();
        return "redirect:/register";
    }

    private boolean checkIfLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getPrincipal() instanceof UserDetails;
    }
    
    private boolean isSameUser(String username){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName().equals(username);
    }

}
