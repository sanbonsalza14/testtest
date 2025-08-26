package com.my.quiz.controller;

import com.my.quiz.dto.UserDto;
import com.my.quiz.entity.UserEntity;
import com.my.quiz.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("login")
    public String loginForm() { return "/user/login"; }

    @PostMapping("login")
    public String login(UserDto dto, HttpSession session, Model model) {
        UserDto loginResult = userService.findOneUser(dto.getEmail());
        if (ObjectUtils.isEmpty(loginResult)) {
            model.addAttribute("msg", "존재하지 않는 계정입니다.");
            return "/user/login";
        }

        // ✅ 수정: 실제 엔티티를 조회해 승인/권한을 확인
        UserEntity entity = userService.findEntity(dto.getEmail());

        if (!dto.getPassword().equals(loginResult.getPassword())) {
            model.addAttribute("msg", "비밀번호가 일치하지 않습니다.");
            return "/user/login";
        }
        if (!entity.isApproved()) {
            model.addAttribute("msg", "관리자 승인 대기중입니다.");
            return "/user/login";
        }

        session.setAttribute("loginEmail", dto.getEmail());
        session.setAttribute("role", entity.getRole().name());
        session.setMaxInactiveInterval(60 * 30);
        return "redirect:/";
    }

    @GetMapping("signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "/user/signup";
    }

    @PostMapping("signup")
    public String signup(@ModelAttribute("user") UserDto dto) {
        userService.saveUser(dto);
        return "redirect:/";
    }

    @GetMapping("list")
    public String userList(Model model) {
        List<UserDto> list = userService.findAllUser();
        model.addAttribute("list", list);
        return "/user/userList";
    }

    @PostMapping("delete")
    public String userDelete(@RequestParam("email") String email) {
        userService.deleteUser(email);
        return "redirect:/user/list";
    }

    @PostMapping("update")
    public String updateUserForm(@RequestParam("email") String email, Model model) {
        UserDto user = userService.findOneUser(email);
        model.addAttribute("user", user);
        return "/user/userUpdate";
    }

    @PostMapping("updateUser")
    public String updateUser(@ModelAttribute("user") UserDto user) {
        userService.saveUser(user);
        return "redirect:/user/list";
    }

    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "main";
    }

    @GetMapping("myInfo")
    public String myInfo(HttpSession session, Model model) {
        Object loginEmail = session.getAttribute("loginEmail");
        if (loginEmail == null) return "redirect:/user/login";
        UserDto user = userService.findOneUser(loginEmail.toString());
        model.addAttribute("user", user);
        return "/user/userUpdate";
    }

    @GetMapping("myPage")
    public String myPage() { return "/user/myPage"; }
}
