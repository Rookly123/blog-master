package com.lz.controller.admin;

import com.lz.entity.Tag;
import com.lz.service.TagService;
import com.lz.service.UserService;
import com.lz.vo.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    //获取分页后的标签
    @GetMapping("/tags")
    public String tags(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        List<Tag> tags=tagService.getAllTags(userid);
        PageInfo pageInfo=new PageInfo<>(tags,7,pageNum);
        model.addAttribute("pageInfo",pageInfo);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        return "admin/tags";
    }

    //跳转到新增页面
    @GetMapping("/tags/input")
    public String input(Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        model.addAttribute("tag", new Tag()); //给新增页面传递一个object对象
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        return "admin/tags-input";
    }

    //跳转到编辑页面,并将要修改的数据回显
    @GetMapping("/tags/{id}/input")
    public String editInput(@PathVariable("id") Long id, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //首先通过编号获取到标签
        Tag tag=tagService.getTagById(id,userid);

        //回显数据
        model.addAttribute("tag",tag);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        return "admin/tags-input";
    }

    //新增标签操作
    @PostMapping("/tags")
    public String post(Tag tag, RedirectAttributes attributes, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //通过标签名获取标签
        String name=tag.getName();
        Tag t= tagService.getTagByName(name,userid);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        //判断标签是否已经存在
        if (t == null ) {
            tagService.saveTag(tag,userid);
            attributes.addFlashAttribute("message", "新增成功");
            return "redirect:/admin/tags";
        } else {
            model.addAttribute("message", "新增失败，标签名重复");
            return "admin/tags-input";
        }
    }

    //编辑标签
    @PostMapping("/tags/{id}")
    public String editPost(Tag tag, RedirectAttributes attributes,Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //通过标签名获取标签
        String name=tag.getName();
        Tag t= tagService.getTagByName(name,userid);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        //判断标签是否已经存在
        if (t == null ) {
            tagService.updateTag(tag,userid);
            attributes.addFlashAttribute("message", "更新成功");
            return "redirect:/admin/tags";
        } else {
            model.addAttribute("message", "更新失败，标签名重复");
            return "admin/tags-input";
        }
    }

    @GetMapping("/tags/{id}/delete")
    public String delete(@PathVariable("id") Long id,RedirectAttributes attributes) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        tagService.deleteTag(id,userid);
        attributes.addFlashAttribute("message", "删除成功");
        return "redirect:/admin/tags";
    }

}
