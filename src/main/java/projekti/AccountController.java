
package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("accounts", accountService.findAll());
        return "register";
    }
    
    @PostMapping("register")
    public String postRegister(@RequestParam String username, @RequestParam String password){
        if (accountService.getAccount(username, false) != null) {
            return "redirect:/register";
        }
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        accountService.addNewAccount(account);
        System.out.println("redirect register account tallennettu");
        return "redirect:/register";
    }
    
}
