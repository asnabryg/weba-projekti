package projekti;

import java.util.Map;
import static jdk.vm.ci.hotspot.HotSpotCompilationRequestResult.success;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private HttpSession session;

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
        if (!password.equals(passwordConfirm)) {
            session.setAttribute("success", "Passwords don't match!");
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
    public String clear(){
        session.invalidate();
        return "redirect:/register";
    }

}
