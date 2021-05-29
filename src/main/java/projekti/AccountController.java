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
    private MessageService messageService;

    @Autowired
    private HttpSession session;
    
    @GetMapping("/profile")
    public String redirectProfile(){
        return "redirect:/profile/" + getUsername() + "/1";
    }
    
    @GetMapping("/profile/{username}")
    public String redirectProfile2(){
        return "redirect:/profile/" + getUsername() + "/1"; 
    }

    @GetMapping("profile/{username}/{pageNumber}")
    public String profile(Model model, @PathVariable String username, @PathVariable Long pageNumber) {
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
        List<Long> imageIds = new ArrayList();
        for (FileObject image : images) {
            imageIds.add(image.getId());
        }
        model.addAttribute("imageIds", imageIds);
        model.addAttribute("following", account.getFollowing());
        model.addAttribute("followers", account.getFollowers());
        model.addAttribute("blockedUsers", account.getBlockedAccounts());
        model.addAttribute("images", account.getImages());
        
        List<Long> Ids = new ArrayList();
        Ids.add(account.getId());
        Long maxPages = (Long) session.getAttribute("maxPages");
        List<Message> messages = messageService.findMessagesByAccounts(Ids, pageNumber);
        model.addAttribute("messages", messages);
        
        model.addAttribute("pageCount", maxPages);
        model.addAttribute("current", pageNumber);
        return "profile";
    }
    
    private void checkSession(List<Long> senderIds) {
        if (session.getAttribute("pageProfile") == null) {
            session.setAttribute("pageProfile", 1L);
        }
        Long messageCount = messageService.getCountBySenders(senderIds);
        if (messageCount == null) 
            messageCount = 0L;
        session.setAttribute("maxPagesProfile", calculateMaxPages(messageCount, 25));
        System.out.println("PageProfile: " + session.getAttribute("pageProfile"));
        System.out.println("MaxPagesProfile: " + session.getAttribute("maxPagesProfile"));
    }
    
    private long calculateMaxPages(long messageCount, long messagesPerPage){
        return (messageCount + messagesPerPage - 1) / messagesPerPage;
    }
    
    @PostMapping("/goToPageInProfile")
    public String goToPage(@RequestParam String page, @RequestParam String username){
        if (!checkIfLoggedIn()) {
            return "redirect:/login";
        }
        System.out.println("username " + username);
        // 0 = next, -1 = previous
        Long sessionPage = (Long) session.getAttribute("pageProfile");
        if (page.equals("next")) {
            sessionPage++;
        }else if (page.equals("previous")){
            sessionPage--;
        }else{
            sessionPage = Long.valueOf(page);
        }
        session.setAttribute("pageProfile", sessionPage);
        return "redirect:/profile/" + username + "/" + sessionPage;
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
        if (!checkIfLoggedIn()) {
            return "redirect:/login";
        }
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
    
    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

}
