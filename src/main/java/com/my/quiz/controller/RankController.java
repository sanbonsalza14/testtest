package com.my.quiz.controller;

import com.my.quiz.entity.UserEntity;
import com.my.quiz.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rank")
public class RankController {

    private final UserRepository userRepository;

    @GetMapping
    public String leaderboard(Model model, HttpSession session) {
        Object email = session.getAttribute("loginEmail");
        if (email == null) return "redirect:/user/login";
        List<UserEntity> list = userRepository.findByApprovedTrueOrderByAnswerTrueDescAnswerFalseAscNicknameAsc();
        model.addAttribute("list", list);
        return "/rank/list";
    }
}
