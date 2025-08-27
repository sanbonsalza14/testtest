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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /** 승인 대기 목록 (기존 유지) */
    @GetMapping("/pending")
    public String pending(Model model, HttpSession session){
        if (!isAdmin(session)) return "redirect:/user/login";
        List<UserEntity> list = userRepository.findAll()
                .stream().filter(u -> u.getRole()== Role.USER && !u.isApproved()).toList();
        model.addAttribute("list", list);
        return "/admin/pending";
    }

    @PostMapping("/approve")
    public String approve(@RequestParam String email, HttpSession session, RedirectAttributes ra){
        if (!isAdmin(session)) return "redirect:/user/login";
        userService.approveUser(email);
        ra.addFlashAttribute("toast", "승인되었습니다: " + email);
        return "redirect:/admin/users";
    }

    @PostMapping("/resetPw")
    public String resetPw(@RequestParam String email, @RequestParam String newPw,
                          HttpSession session, RedirectAttributes ra){
        if (!isAdmin(session)) return "redirect:/user/login";
        String pw = (newPw == null) ? "" : newPw.trim();
        if (pw.length() < 3) {
            ra.addFlashAttribute("toast", "비밀번호는 최소 3자 이상이어야 합니다.");
            return "redirect:/admin/users";
        }
        userService.updatePasswordByAdmin(email, pw);
        ra.addFlashAttribute("toast", "비밀번호가 변경되었습니다: " + email);
        return "redirect:/admin/users";
    }

    @GetMapping("/nicknames")
    public String nicknames(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/user/login";
        model.addAttribute("list", userRepository.findAll());
        return "/admin/nicknames";
    }

    @PostMapping("/nickname/update")
    public String updateNickname(@RequestParam String email, @RequestParam String nickname,
                                 HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/user/login";
        try {
            userService.updateNicknameByAdmin(email, nickname.trim());
            ra.addFlashAttribute("toast", "닉네임이 변경되었습니다: " + nickname);
        } catch (Exception e) {
            ra.addFlashAttribute("toast", "실패: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ✅ 회원 관리 통합 화면
    @GetMapping("/users")
    public String users(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/user/login";
        List<UserEntity> all = userRepository.findAllByOrderByRoleAscApprovedDescNicknameAsc();
        model.addAttribute("list", all);
        return "/admin/users";
    }

    /** ✅ 회원 삭제 (ADMIN 계정은 삭제 버튼 자체를 숨김) */
    @PostMapping("/user/delete")
    public String deleteUser(@RequestParam String email, HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/user/login";
        try {
            userService.deleteUser(email);
            ra.addFlashAttribute("toast", "삭제 완료: " + email);
        } catch (Exception e) {
            ra.addFlashAttribute("toast", "삭제 실패: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
