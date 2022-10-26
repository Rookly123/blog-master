package com.lz.controller.admin;

import com.lz.entity.Blog;
import com.lz.entity.Type;
import com.lz.entity.User;
import com.lz.service.UserService;
import com.lz.vo.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class UserController {


    @Autowired
    private UserService userService;

    @GetMapping("/check")
    public String check()
    {
        return "redirect:/admin/blogs";
    }

    @GetMapping("/users")
    public String users(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        if(userService.getUserById(userid).getType()!=1)
        {
            return "redirect:/admin/blogs";
        }
        //获取分类列
        List<User> users = userService.getAllUser();
        PageInfo pageInfo=new PageInfo<>(users,7,pageNum);
        model.addAttribute("pageInfo",pageInfo);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        return "admin/users";
    }

    @GetMapping("/users/{id}/update/{type}")
    public String editInput(@PathVariable("id") Long id, Model model,@PathVariable("type") Integer type) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();

        userService.updateUserStatus(id,type);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
            model.addAttribute("message","修改成功");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/myselfupdate/{id}")
    public String myselfupdate(@PathVariable("id") Long id,Model model,@RequestParam("password1") String password1){
        userService.updateUser(id,password1);
        model.addAttribute("message","修改成功");
        return "redirect:/logout";
    }

    @GetMapping("/myself")
    public String myself(Model model)
    {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        model.addAttribute("user",userService.getUserById(userid));
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        return "admin/myself";
    }
}
