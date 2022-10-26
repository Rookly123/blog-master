package com.lz.controller;

import com.lz.entity.User;
import com.lz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UUserController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


    @GetMapping("/useradd")
    public String registerPage(){
        return "register";
    }

    @PostMapping("/loginCheck")
    public String loginCheck(RedirectAttributes attributes, @RequestParam("code") String code, @RequestParam("password") String password,@RequestParam("username") String username, HttpSession session)
    {
        if(!getCheckCaptcha(code,session))
        {
            attributes.addFlashAttribute("message", "验证码和图片不一致，登录失败");
            System.out.println(attributes.getFlashAttributes().get("message"));
            return "redirect:/logout";
        }
        else if(userService.login(username,password) == null)
        {
            attributes.addFlashAttribute("message", "无此用户或者密码出错");
            System.out.println(attributes.getFlashAttributes().get("message"));
            return "redirect:/logout";
        }
        else if(userService.getUser(username).getType() == 3)
        {
            attributes.addFlashAttribute("message", "该账户已经被封禁");
            System.out.println(attributes.getFlashAttributes().get("message"));
            return "redirect:/logout";
        }
        else
        {
//            User user = userService.login(username, password);
//            user.setPassword(null);
//            session.setAttribute("user",user);
            return "redirect:/";
        }
    }

    @PostMapping("/addCheck")
    public String addCheck(RedirectAttributes attributes, @RequestParam("code") String code,@RequestParam("nickname") String nickname, @RequestParam("password1") String password1, @RequestParam("password2") String password2, @RequestParam("username") String username, HttpSession session){
        System.out.println(code);
        if(!getCheckCaptcha(code,session))
        {
            attributes.addFlashAttribute("message", "验证码和图片不一致，用户注册失败");
            return "redirect:/useradd";
        }
        else if(userService.getUser(username)!=null)
        {
            attributes.addFlashAttribute("message", "用户名已经存在，用户注册失败");
            return "redirect:/useradd";
        }
        else
        {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            User user = new User(null,nickname,username,encoder.encode(password1),"/images/me.jpg",2);
            userService.saveUser(user);
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logoutPage(RedirectAttributes attributes) {
        System.out.println(attributes.getFlashAttributes().size());
        return "redirect:/login";
    }

    public boolean getCheckCaptcha(String code, HttpSession session) {

        try {
            //toLowerCase() 不区分大小写进行验证码校验
            String sessionCode = String.valueOf(session.getAttribute("JCCODE")).toLowerCase();
            String receivedCode = code.toLowerCase();
            return !sessionCode.equals("") && !receivedCode.equals("") && sessionCode.equals(receivedCode);

        } catch (Exception e) {
            return false;
        }
    }
}
