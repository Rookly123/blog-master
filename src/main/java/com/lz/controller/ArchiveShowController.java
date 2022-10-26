package com.lz.controller;

import com.lz.service.BlogService;
import com.lz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * @author lize
 */
@Controller
public class ArchiveShowController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @GetMapping("/archives")
    public String archives(Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        model.addAttribute("archiveMap", blogService.archiveBlog(userid));
        model.addAttribute("blogCount", blogService.countBlog(userid));
        return "archives";
    }
}
