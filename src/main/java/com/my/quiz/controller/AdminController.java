// com.my.quiz.controller.AdminController
package com.my.quiz.controller;

import com.my.quiz.entity.Role;
import com.my.quiz.entity.UserEntity;
import com.my.quiz.repository.UserRepository;
import com.my.quiz.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserRepository userRepository;
    private final UserService userService;

    private boolean isAdmin(HttpSession session){
        Object r = session.getAttribute("role");
        return r != null && "ADMIN".equals(r.toString());
    }

    @GetMapping("/pending")
    public String pending(Model model, HttpSession session){
        if (!isAdmin(session)) return "redirect:/";
        List<UserEntity> list = userRepository.findAll()
                .stream().filter(u -> u.getRole()== Role.USER && !u.isApproved()).toList();
        model.addAttribute("list", list);
        return "/admin/pending";
    }

    @PostMapping("/approve")
    public String approve(@RequestParam String email, HttpSession session){
        if (!isAdmin(session)) return "redirect:/";
        userService.approveUser(email);
        return "redirect:/admin/pending";
    }

    @PostMapping("/resetPw")
    public String resetPw(@RequestParam String email, @RequestParam String newPw, HttpSession session){
        if (!isAdmin(session)) return "redirect:/";
        userService.updatePasswordByAdmin(email, newPw);
        return "redirect:/admin/pending";
    }
}
