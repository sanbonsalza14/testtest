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

    // 로그인 폼
    @GetMapping("login")
    public String loginForm() {
        return "/user/login";
    }

    // 로그인 처리  **<= 이 메서드만 /user/login POST 로 남겨두세요**
    @PostMapping("login")
    public String login(UserDto dto, HttpSession session, Model model) {
        UserDto loginResult = userService.findOneUser(dto.getEmail());
        if (ObjectUtils.isEmpty(loginResult)) {
            model.addAttribute("msg", "존재하지 않는 계정입니다.");
            return "/user/login";
        }

        // 엔티티(승인/역할 확인용)
        UserEntity entity = UserDto.toDto(loginResult);

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

    // 회원가입 폼
    @GetMapping("signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "/user/signup";
    }

    // 회원가입 처리
    @PostMapping("signup")
    public String signup(@ModelAttribute("user") UserDto dto) {
        userService.saveUser(dto);
        return "redirect:/";
    }

    // 회원 목록
    @GetMapping("list")
    public String userList(Model model) {
        List<UserDto> list = userService.findAllUser();
        model.addAttribute("list", list);
        return "/user/userList";
    }

    // 회원 삭제
    @PostMapping("delete")
    public String userDelete(@RequestParam("email") String email) {
        userService.deleteUser(email);
        return "redirect:/user/list";
    }

    // 회원 수정 폼
    @PostMapping("update")
    public String updateUserForm(@RequestParam("email") String email, Model model) {
        UserDto user = userService.findOneUser(email);
        model.addAttribute("user", user);
        return "/user/userUpdate";
    }

    // 회원 수정 저장
    @PostMapping("updateUser")
    public String updateUser(@ModelAttribute("user") UserDto user) {
        userService.saveUser(user);
        return "redirect:/user/list";
    }

    // 로그아웃
    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "main";
    }

    // 내 정보 보기/수정
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