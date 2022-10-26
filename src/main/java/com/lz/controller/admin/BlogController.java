package com.lz.controller.admin;


import com.lz.entity.Blog;
import com.lz.entity.User;
import com.lz.service.BlogService;
import com.lz.service.TagService;
import com.lz.service.TypeService;
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
public class BlogController {

    @Autowired
    private BlogService blogService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    //定义一个公共方法，获取分类和标签
    private void setTypeAndTag(Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        model.addAttribute("types", typeService.getAllType(userid));
        model.addAttribute("tags", tagService.getAllTag(userid));
    }

    //后台获取博客列表
    @GetMapping("/blogs")
    public String blogs(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //获取分类列表
        model.addAttribute("types", typeService.getAllType(userid));

        //获取博客列表
        List<Blog> blogs=blogService.getAllBlog(userid);
        PageInfo pageInfo=new PageInfo<>(blogs,7,pageNum);
        model.addAttribute("pageInfo",pageInfo);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        return "admin/blogs";
    }

    //后台搜索博客列表
    @PostMapping("/blogs/search")
    public String search(@RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,Blog blog, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        List<Blog> blogs=blogService.searchBlog(blog);
        PageInfo pageInfo=new PageInfo<>(blogs,7,pageNum);

        setTypeAndTag(model);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("message", "查询成功");
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }

        return "admin/blogs";
    }

    //跳转到博客新增页面，同时将分类和标签显示出来
    @GetMapping("/blogs/input")
    public String input(Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        setTypeAndTag(model);
        model.addAttribute("blog", new Blog());
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        return "admin/blogs-input";
    }

    //新增,编辑博客
    @PostMapping("/blogs")
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //设置用户编号
        blog.setUserId(userid);

        //设置分类编号
        blog.setType(typeService.getTypeById(blog.getType().getId(),userid));
        blog.setTypeId(blog.getType().getId());

        if (blog.getId() == null) {
            //新增
            blogService.saveBlog(blog);
            attributes.addFlashAttribute("message", "操作成功");
        } else {
            //更新
            blogService.updateBlog(blog);
            attributes.addFlashAttribute("message", "操作成功");
        }
        return "redirect:/admin/blogs";
    }


    //跳转到更新页面
    @GetMapping("/blogs/{id}/input")
    public String editInput(@PathVariable("id") Long id, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        setTypeAndTag(model);
        Blog blog = blogService.getBlogById(id,userid);
        blog.setType(typeService.getTypeById(blog.getTypeId(),userid));
        model.addAttribute("blog",blog);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        return "admin/blogs-input";
    }


    //删除博客
    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable("id") Long id,RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "删除成功");
        return "redirect:/admin/blogs";
    }



}
