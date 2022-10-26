package com.lz.controller;

import com.lz.entity.Blog;
import com.lz.entity.Type;
import com.lz.service.BlogService;
import com.lz.service.TypeService;
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
public class TypeShowController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @GetMapping("/types/{id}")
    public String types(@RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum, @PathVariable("id") Long id, Model model) {

        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //获取所有分类
        List<Type> types=typeService.getAllType(userid);

        List<Blog> blogs;
        //博客首页点击more进来的
        if (id == -1) {
            if(types.size() <= 0)
            {
                id = Long.valueOf(-1);
            }
            else {
                //自动选择第一个分类，显示其下的博客
                id = types.get(0).getId();
            }
        }
        if(id != -1) {
            blogs = blogService.getBlogByTypeId(id,userid);
        }
        else
        {
            blogs = new ArrayList<>();
        }
        PageInfo pageInfo=new PageInfo<>(blogs,7,pageNum);

        for(Type type:types){
            type.setBlogs(blogService.getBlogByTypeId(type.getId(),userid));
        }

        model.addAttribute("types", types);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("activeTypeId", id);
        model.addAttribute("userid",userid);
        return "types";
    }
}
