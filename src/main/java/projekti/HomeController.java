package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
public class HomeController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private HttpSession session;

    private void checkSession(List<Long> senderIds) {
        if (session.getAttribute("page") == null) {
            session.setAttribute("page", 1L);
        }
        Long messageCount = messageService.getCountBySenders(senderIds);
        if (messageCount == null) 
            messageCount = 0L;
        session.setAttribute("maxPages", calculateMaxPages(messageCount, 25));
        System.out.println("Page: " + session.getAttribute("page"));
        System.out.println("MaxPages: " + session.getAttribute("maxPages"));
    }
    
    private long calculateMaxPages(long messageCount, long messagesPerPage){
        return (messageCount + messagesPerPage - 1) / messagesPerPage;
    }

    @GetMapping("/home")
    public String homeRedirect(Model model) {
        if (!checkIfLoggedIn()) {
            return "redirect:/login";
        }
        Long page = 1L;
        if (session.getAttribute("page") != null) {
            page = (Long) session.getAttribute("page");
        }
        return "redirect:/home/" + page;
    }

    @GetMapping("/home/{pageNumber}")
    public String home(Model model, @PathVariable Long pageNumber) {
        if (!checkIfLoggedIn()) {
            return "redirect:/login";
        }
        session.setAttribute("page", pageNumber);
        List<Long> followingIds = new ArrayList();
        Account account = accountService.getAccount(getUsername(), false);
        for (Account acc : account.getFollowing()){
            followingIds.add(acc.getId());
        }
        followingIds.add(account.getId());
        checkSession(followingIds);
        Long maxPages = (Long) session.getAttribute("maxPages");
        List<Message> messages = messageService.findMessagesByAccounts(followingIds, pageNumber);
        model.addAttribute("account", account);
        model.addAttribute("messages", messages);
        model.addAttribute("pageCount", maxPages);
        model.addAttribute("current", pageNumber);
        return "home";
    }
    
    @PostMapping("/goToPage")
    public String goToPage(@RequestParam String page){
        if (!checkIfLoggedIn()) {
            return "redirect:/index";
        }
        // 0 = next, -1 = previous
        Long sessionPage = (Long) session.getAttribute("page");
        if (page.equals("next")) {
            sessionPage++;
        }else if (page.equals("previous")){
            sessionPage--;
        }else{
            sessionPage = Long.valueOf(page);
        }
        session.setAttribute("page", sessionPage);
        return "redirect:/home/" + sessionPage;
    }

    @PostMapping("/writeMessage")
    public String writeMessage(@RequestParam String messageText) {
        if (!checkIfLoggedIn()) {
            return "redirect:/index";
        }
        Message message = new Message();
        message.setText(messageText);
        LocalDateTime date = LocalDateTime.now();
        message.setDate(date);
        message.setDateInString(dateToString(date));
        Account account = accountService.getAccount(getUsername(), false);
        message.setSender(account);
        messageService.save(message);
        return "redirect:/home";
    }

    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private boolean checkIfLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getPrincipal() instanceof UserDetails;
    }

    private boolean isSameUser(String username) {
        return getUsername().equals(username);
    }
    
    private String dateToString(LocalDateTime d){
        String hours = addZero(String.valueOf(d.getHour()));
        String minutes = addZero(String.valueOf(d.getMinute()));
        
        String day = addZero(String.valueOf(d.getDayOfMonth()));
        String month = addZero(String.valueOf(d.getMonthValue()));
        String year = String.valueOf(d.getYear());

        return hours + ":" + minutes + " - " + day + "." + month + "." + year;
    }
    
    private String addZero(String s){
        if (Integer.valueOf(s) < 10) {
            return "0" + s;
        }
        return s;
    }
}
