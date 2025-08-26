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

    /** 승인 대기 목록 */
    @GetMapping("/pending")
    public String pending(Model model, HttpSession session){
        if (!isAdmin(session)) return "redirect:/user/login";
        List<UserEntity> list = userRepository.findAll()
                .stream().filter(u -> u.getRole()== Role.USER && !u.isApproved()).toList();
        model.addAttribute("list", list);
        return "/admin/pending";
    }

    /** 승인 처리 → 홈으로 이동 + 메시지 노출 */
    @PostMapping("/approve")
    public String approve(@RequestParam String email,
                          HttpSession session,
                          RedirectAttributes ra){
        if (!isAdmin(session)) return "redirect:/user/login";
        userService.approveUser(email);
        ra.addFlashAttribute("toast", "승인되었습니다: " + email);
        return "redirect:/";
    }

    /** 비번 초기화 → 홈으로 이동 + 메시지 노출 */
    @PostMapping("/resetPw")
    public String resetPw(@RequestParam String email,
                          @RequestParam String newPw,
                          HttpSession session,
                          RedirectAttributes ra){
        if (!isAdmin(session)) return "redirect:/user/login";

        String pw = (newPw == null) ? "" : newPw.trim();
        if (pw.length() < 3) {
            ra.addFlashAttribute("toast", "비밀번호는 최소 3자 이상이어야 합니다.");
            return "redirect:/admin/pending";
        }

        userService.updatePasswordByAdmin(email, pw);
        ra.addFlashAttribute("toast", "비밀번호가 변경되었습니다: " + email);
        return "redirect:/";
    }
}
