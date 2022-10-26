package com.lz.controller.admin;

import com.lz.entity.Type;
import com.lz.service.TypeService;
import com.lz.service.UserService;
import com.lz.vo.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class TypeController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private UserService userService;

    //获取分页后的分类
    @GetMapping("/types")
    public String types(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        List<Type> types=typeService.getAllType(userid);
        PageInfo pageInfo=new PageInfo<>(types,7,pageNum);
        model.addAttribute("pageInfo",pageInfo);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        return "admin/types";
    }

    //跳转到新增页面
    @GetMapping("/types/input")
    public String input(Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        model.addAttribute("type", new Type()); //给新增页面传递一个object对象
        return "admin/types-input";
    }

    //跳转到编辑页面,并将要修改的数据回显
    @GetMapping("/types/{id}/input")
    public String editInput(@PathVariable("id") Long id, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //首先通过编号获取到分类
        Type type=typeService.getTypeById(id,userid);

        //回显数据
        model.addAttribute("type",type);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        return "admin/types-input";
    }

    //新增分类操作
    @PostMapping("/types")
    public String post(Type type, RedirectAttributes attributes, Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //通过分类名获取分类
        String name=type.getName();
        Type t= typeService.getTypeByName(name,userid);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        //判断分类是否已经存在
        if (t == null ) {
            typeService.saveType(type,userid);
            attributes.addFlashAttribute("message", "新增成功");
            return "redirect:/admin/types";
        } else {
            model.addAttribute("message", "新增失败，分类名重复");
            return "admin/types-input";
        }
    }

    //编辑分类
    @PostMapping("/types/{id}")
    public String editPost(Type type, RedirectAttributes attributes,Model model) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        //通过分类名获取分类
        String name=type.getName();
        Type t= typeService.getTypeByName(name,userid);
        if(userService.getUserById(userid).getType() == 1) {
            model.addAttribute("isOk", true);
        }
        //判断分类是否已经存在
        if (t == null ) {
            typeService.updateType(type,userid);
            attributes.addFlashAttribute("message", "更新成功");
            return "redirect:/admin/types";
        } else {
            model.addAttribute("message", "更新失败，分类名重复");
            return "admin/types-input";
        }
    }

    @GetMapping("/types/{id}/delete")
    public String delete(@PathVariable("id") Long id,RedirectAttributes attributes) {
        Long userid = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        typeService.deleteType(id,userid);
        attributes.addFlashAttribute("message", "删除成功");
        return "redirect:/admin/types";
    }

}
