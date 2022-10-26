package com.lz.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lz.entity.*;
import com.lz.service.*;
import org.apache.catalina.authenticator.SavedRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author lize
 */
@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(@RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum, Model model) {
        PageHelper.startPage(pageNum,7);
        //前台获取博客
        List<Blog> blogs=blogService.getIndexBlog();
        PageInfo pageInfo=new PageInfo<>(blogs);
        model.addAttribute("pageInfo",pageInfo);
        return "index";
    }


    @PostMapping("/search")
    public String search(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,
                         @RequestParam String query, Model model) {
        PageHelper.startPage(pageNum,7);
        List<Blog> blogs=blogService.searchIndexBlog(query);
        PageInfo pageInfo=new PageInfo<>(blogs);

        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("query", query);
        return "search";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable("id") Long id, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        Blog blog=blogService.getDetailedBlog(id,userid);
        model.addAttribute("blog", blog);
        model.addAttribute("nickname",userService.getUserById(userid).getNickname());
        return "blog";
    }

    @GetMapping("/myself/{id}")
    public String myself(@RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,@PathVariable("id") Long id, Model model)
    {
        int flag = 0;
        if(id == -1)
        {
            flag = 1;
            id = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        }
        List<Blog> blogs = blogService.getBlogBymyself(id);
        com.lz.vo.PageInfo pageInfo=new com.lz.vo.PageInfo<>(blogs,7,pageNum);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("id",id);
        System.out.println("id :" + id);
        if(flag == 1) {
            return "myself";
        }
        else
        {
            return "person";
        }
    }
}
