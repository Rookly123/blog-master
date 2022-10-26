package com.lz.controller;

import com.lz.entity.Blog;
import com.lz.entity.Tag;
import com.lz.service.BlogService;
import com.lz.service.TagService;
import com.lz.service.UserService;
import com.lz.vo.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TagShowController {

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/tags/{id}")
    public String tags(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum, @PathVariable("id") Long id, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //获取所有标签
        List<Tag> tags=tagService.getAllTag(userid);
        //博客首页点击more进来的
        List<Blog> blogs;
        //博客首页点击more进来的
        if (id == -1) {
            if(tags.size() <= 0)
            {
                id = Long.valueOf(-1);
            }
            else {
                //自动选择第一个分类，显示其下的博客
                id = tags.get(0).getId();
            }
        }
        if(id != -1) {
            blogs = blogService.getBlogByTagId(id,userid);
        }
        else
        {
            blogs = new ArrayList<>();
        }
        PageInfo pageInfo=new PageInfo<>(blogs,7,pageNum);
        for(Tag tag:tags){
            tag.setBlogs(blogService.getBlogByTagId(tag.getId(),userid));
        }
//        pageInfo = new PageInfo(page.getResult());
        model.addAttribute("tags", tags);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("activeTagId", id);
        return "tags";
    }

    @GetMapping("/tagss/{id}")
    public String tagss(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum, @PathVariable("id") Long id, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //获取所有标签
        List<Tag> tags=tagService.getAllTags(userid);
        //博客首页点击more进来的
        List<Blog> blogs;
        //博客首页点击more进来的
        if (id == -1) {
            if(tags.size() <= 0)
            {
                id = Long.valueOf(-1);
            }
            else {
                //自动选择第一个分类，显示其下的博客
                id = tags.get(0).getId();
            }
        }
        if(id != -1) {
            blogs = blogService.getBlogByTagId(id,userid);
        }
        else
        {
            blogs = new ArrayList<>();
        }
        PageInfo pageInfo=new PageInfo<>(blogs,7,pageNum);
        for(Tag tag:tags){
            tag.setBlogs(blogService.getBlogByTagId(tag.getId(),userid));
        }
//        pageInfo = new PageInfo(page.getResult());
        model.addAttribute("tags", tags);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("userid",userid);
        model.addAttribute("activeTagId", id);
        return "tags";
    }
}
