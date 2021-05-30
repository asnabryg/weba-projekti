package projekti;

import java.time.LocalDateTime;
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
import javax.transaction.Transactional;
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
    private FileObjectService fileService;

    @Autowired
    private FollowService followService;

    @Autowired
    private HttpSession session;

    @GetMapping("/profile")
    public String redirectProfile() {
        if (!checkIfLoggedIn()) {
            return "redirect:/login";
        }
        return "redirect:/profile/" + getUsername() + "/1";
    }

    @GetMapping("/profile/{username}")
    public String redirectProfile2(@PathVariable String username) {
        return "redirect:/profile/" + username + "/1";
    }

    @GetMapping("profile/{username}/{pageNumber}")
    public String profile(Model model, @PathVariable String username, @PathVariable Long pageNumber) {
        model.addAttribute("sameUser", isSameUser(username));
        model.addAttribute("logged", checkIfLoggedIn());
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

        List<Long> voteCounts = new ArrayList();
        for (FileObject image : images) {
            boolean voted = false;
            Long count = 0L;
            for (ImageVote vote : image.getImageVotes()) {
                if (vote.getGiver().getUsername().equals(account.getUsername())) {
                    voted = vote.isVoted();
                }
                if (vote.isVoted()) {
                    count++;
                }
            }
            voteCounts.add(count);
        }
        model.addAttribute("voteCounts", voteCounts);

        model.addAttribute("imageIds", imageIds);
        model.addAttribute("following", followService.findAllFollowings(account));
        model.addAttribute("followers", followService.findAllFollowers(account));
        List<Integer> statuses = new ArrayList();
        statuses.add(-1);
        statuses.add(-3);
        List<Follow> blocked = followService.findAllByFollowerAndStatus(accountService.getAccount(getUsername(), false), statuses);
        model.addAttribute("blocked", blocked);
        if (!isSameUser(username)) {
            Integer status = 0;
            Follow ownFollow = followService.findByFollowerAndFollowing(accountService.getAccount(getUsername(), false), account);
            if (ownFollow != null) {
                status = ownFollow.getStatus();
            }
            model.addAttribute("followStatus", status);

        }
        model.addAttribute("images", account.getImages());
        model.addAttribute("showImage", false);

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
        if (messageCount == null) {
            messageCount = 0L;
        }
        session.setAttribute("maxPagesProfile", calculateMaxPages(messageCount, 25));
        System.out.println("PageProfile: " + session.getAttribute("pageProfile"));
        System.out.println("MaxPagesProfile: " + session.getAttribute("maxPagesProfile"));
    }

    private long calculateMaxPages(long messageCount, long messagesPerPage) {
        return (messageCount + messagesPerPage - 1) / messagesPerPage;
    }

    @PostMapping("/goToPageInProfile")
    public String goToPage(@RequestParam String page, @RequestParam String username) {
        if (!checkIfLoggedIn()) {
            return "redirect:/login";
        }
        System.out.println("username " + username);
        // 0 = next, -1 = previous
        Long sessionPage = (Long) session.getAttribute("pageProfile");
        if (page.equals("next")) {
            sessionPage++;
        } else if (page.equals("previous")) {
            sessionPage--;
        } else {
            sessionPage = Long.valueOf(page);
        }
        session.setAttribute("pageProfile", sessionPage);
        return "redirect:/profile/" + username + "/" + sessionPage;
    }

    @GetMapping("/profile/{username}/image/{imageId}")
    public String showImage(Model model, @PathVariable String username, @PathVariable String imageId) {
        FileObject image = fileService.getFileObject(Long.valueOf(imageId));
        boolean voted = false;
        Long count = 0L;
        for (ImageVote vote : image.getImageVotes()) {
            if (vote.getGiver().getUsername().equals(getUsername())) {
                voted = vote.isVoted();
            }
            if (vote.isVoted()) {
                count++;
            }
        }
        model.addAttribute("isVoted", voted);
        model.addAttribute("voteCount", count);
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
        model.addAttribute("profileImage", profileImage);
        List<Integer> statuses = new ArrayList();
        statuses.add(-1);
        statuses.add(-3);
        List<Follow> blocked = followService.findAllByFollowerAndStatus(accountService.getAccount(getUsername(), false), statuses);
        System.out.println("blocked " + blocked.size());
        model.addAttribute("following", followService.findAllFollowings(account));
        model.addAttribute("followers", followService.findAllFollowers(account));
        model.addAttribute("blocked", blocked);
        model.addAttribute("image", image);
        model.addAttribute("showImage", true);
        return "profile";
    }

    @PostMapping("/follow/{username}")
    public String followUser(@PathVariable String username) {
        Account follower = accountService.getAccount(getUsername(), false);
        Account following = accountService.getAccount(username, false);
        Follow follow = followService.findByFollowerAndFollowing(follower, following);
        if (follow == null) {
            follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            follow.setStatus(1);
        } else {
            if (follow.getStatus() == 0 || follow.getStatus() == -1) {
                follow.setStatus(1);
            } else if (follow.getStatus() == 1) {
                follow.setStatus(0);
            } else {
                return "redirect:/profile/" + username;
            }
        }
        followService.save(follow);

        return "redirect:/profile/" + username;
    }

    @PostMapping("/block/{username}")
    public String blockUser(@PathVariable String username) {
        Account blocker = accountService.getAccount(getUsername(), false);
        Account blocking = accountService.getAccount(username, false);
        Follow followBlocker = followService.findByFollowerAndFollowing(blocker, blocking);
        Follow followBlocking = followService.findByFollowerAndFollowing(blocking, blocker);
        if (followBlocker == null) {
            //block
            followBlocker = new Follow();
            followBlocker.setFollower(blocker);
            followBlocker.setFollowing(blocking);
            followBlocker.setStatus(-1);
            followBlocker.setDate(LocalDateTime.now());
            if (followBlocking == null) {
                followBlocking = new Follow();
            }
            followBlocking.setFollower(blocking);
            followBlocking.setFollowing(blocker);
            followBlocking.setStatus(-2);
            followBlocking.setDate(LocalDateTime.now());
            followService.save(followBlocker);
            followService.save(followBlocking);
            return "redirect:/profile/" + username;
        } else {
            if (followBlocking != null) {
                if (followBlocker.getStatus() >= 0) {
                    //block
                    followBlocker.setStatus(-1);
                    followBlocker.setDate(LocalDateTime.now());
                    followBlocking.setStatus(-2);
                    followBlocking.setDate(LocalDateTime.now());
                } else if (followBlocker.getStatus() == -2) {
                    //block
                    followBlocker.setStatus(-3);
                    followBlocker.setDate(LocalDateTime.now());
                    followBlocking.setStatus(-3);
                    followBlocking.setDate(LocalDateTime.now());
                } else if (followBlocker.getStatus() == -1) {
                    //unblock
                    followBlocker.setStatus(0);
                    followBlocker.setDate(LocalDateTime.now());
                    followBlocking.setStatus(0);
                    followBlocking.setDate(LocalDateTime.now());

                } else if (followBlocker.getStatus() == -3) {
                    //unblock
                    followBlocker.setStatus(-2);
                    followBlocker.setDate(LocalDateTime.now());
                    followBlocking.setStatus(-1);
                    followBlocking.setDate(LocalDateTime.now());
                }
                followService.save(followBlocker);
                followService.save(followBlocking);
                return "redirect:/profile/" + username;
            } else {
                //block
                followBlocker.setStatus(-1);
                followBlocker.setDate(LocalDateTime.now());
                followBlocking = new Follow();
                followBlocking.setFollower(blocking);
                followBlocking.setFollowing(blocker);
                followBlocking.setStatus(-2);
                followBlocking.setDate(LocalDateTime.now());
                followService.save(followBlocker);
                followService.save(followBlocking);
                return "redirect:/profile/" + username;
            }
        }
    }

    @PostMapping("/changeNickname")
    public String newNickname(@RequestParam String newNickname) {
        if (!checkIfLoggedIn()) {
            session.setAttribute("logged", false);
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

    private boolean isSameUser(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName().equals(username);
    }

    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

}
