package com.my.quiz.controller;

import com.my.quiz.dto.QuizDto;
import com.my.quiz.service.QuizService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;

    private String emailOf(HttpSession session){
        Object a = session.getAttribute("loginEmail");
        return (a == null) ? null : a.toString();
    }

    @GetMapping
    public String list(Model model, HttpSession session){
        List<QuizDto> list = quizService.findAll();
        model.addAttribute("list", list);
        model.addAttribute("loginEmail", session.getAttribute("loginEmail"));
        return "quiz/showQuiz";
    }

    @GetMapping("/insertForm")
    public String insertForm(Model model){
        model.addAttribute("quiz", new QuizDto());
        return "quiz/insertForm";
    }

    @PostMapping("/insert")
    public String insert(@Valid @ModelAttribute("quiz") QuizDto dto,
                         BindingResult bindingResult,
                         HttpSession session){
        if (bindingResult.hasErrors()) return "quiz/insertForm";
        String email = emailOf(session);
        if (email == null) return "redirect:/user/login";
        dto.setWriter(email);
        quizService.insert(dto);
        return "redirect:/quiz";
    }

    @GetMapping("/{id}")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session){
        QuizDto dto = quizService.findOne(id);
        if (ObjectUtils.isEmpty(dto)) return "redirect:/quiz";
        String email = emailOf(session);
        if (email == null || !quizService.isOwner(dto, email)) return "redirect:/quiz";
        model.addAttribute("quiz", dto);
        return "quiz/updateForm";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("quiz") QuizDto dto,
                         BindingResult bindingResult,
                         HttpSession session){
        if (bindingResult.hasErrors()) return "quiz/updateForm";
        String email = emailOf(session);
        if (email == null || !quizService.isOwner(dto.getId(), email)) return "redirect:/quiz";
        quizService.updatePreserveWriter(dto);
        return "redirect:/quiz";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, HttpSession session){
        String email = emailOf(session);
        if (email == null || !quizService.isOwner(id, email)) return "redirect:/quiz";
        quizService.delete(id);
        return "redirect:/quiz";
    }

    @GetMapping("/play")
    public String play(Model model){
        QuizDto q = quizService.pickRandom();
        model.addAttribute("quiz", q);
        return "quiz/play";
    }

    @PostMapping("/check")
    public String check(@RequestParam Long id,
                        @RequestParam String answer,
                        HttpSession session,
                        Model model){
        String email = emailOf(session); // null이면 guest로 저장됨
        boolean correct = quizService.checkAnswer(id, answer, email);
        model.addAttribute("correct", correct);
        return "quiz/check";
    }
}