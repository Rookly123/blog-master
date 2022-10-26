package com.lz.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lz.entity.Blog;
import com.lz.entity.Comment;
import com.lz.entity.User;
import com.lz.service.BlogService;
import com.lz.service.CommentService;
import com.lz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @Value("${comment.avatar}")
    private String avatar;

    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable("blogId") Long blogId, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        List<Comment> comments = commentService.getCommentByBlogId(blogId);
        model.addAttribute("comments", comments);
        model.addAttribute("nickname",userService.getUserById(userid).getNickname());
        model.addAttribute("blog", blogService.getDetailedBlog(blogId,userid));
        return "blog :: commentList";
    }



    @PostMapping("/comments")
    public String post(Comment comment) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //获取博客编号
        Long blogId = comment.getBlog().getId();
        comment.setBlogId(blogId);

        //获取被回复的昵称
        String replyName=comment.getReplyName();
        if(replyName.equals("")){
            comment.setReplyName(null);
        }
        comment.setAvatar(userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getAvatar());
        comment.setAdminComment(true);
        comment.setUserId(userid);
        commentService.saveComment(comment);

        return "redirect:/comments/" + blogId;
    }



}
