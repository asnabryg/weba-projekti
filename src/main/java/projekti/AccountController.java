package projekti;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSession session;

    @GetMapping("/login")
    public String login(Model model, @RequestParam Map<String, String> params) {
        if (checkIfLoggedIn()) {
            if (params.containsKey("logout")) {
                return "login";
            }
            return "redirect:/index";
        }
        if (params.containsKey("error")) {
            model.addAttribute("error", params.get("error").equals("true"));
        }
        return "login";
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
        accountService.addNewAccount(account);
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

}
