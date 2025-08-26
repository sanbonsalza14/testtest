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

    /** 라운드당 문제 수 */
    private static final int MAX_ROUNDS = 20;

    // ===== 세션 헬퍼 =====
    private String emailOf(HttpSession s){
        Object a = s.getAttribute("loginEmail");
        return (a == null) ? null : a.toString();
    }
    private int getInt(HttpSession s, String key){
        Object v = s.getAttribute(key);
        return (v instanceof Integer) ? (Integer)v : 0;
    }
    private void setInt(HttpSession s, String key, int n){ s.setAttribute(key, n); }

    private int played(HttpSession s){ return getInt(s, "played"); }
    private void played(HttpSession s, int n){ setInt(s, "played", n); }

    private int roundCorrect(HttpSession s){ return getInt(s, "roundCorrect"); }
    private void roundCorrect(HttpSession s, int n){ setInt(s, "roundCorrect", n); }

    private int roundWrong(HttpSession s){ return getInt(s, "roundWrong"); }
    private void roundWrong(HttpSession s, int n){ setInt(s, "roundWrong", n); }

    private void incRoundBy(HttpSession s, boolean correct){
        if (correct) roundCorrect(s, roundCorrect(s) + 1);
        else roundWrong(s, roundWrong(s) + 1);
    }

    // ===== CRUD 목록 =====
    @GetMapping
    public String list(Model model, HttpSession session){
        List<QuizDto> list = quizService.findAll();
        model.addAttribute("list", list);
        model.addAttribute("loginEmail", session.getAttribute("loginEmail"));
        return "quiz/showQuiz";
    }

    // ===== 등록 폼 =====
    @GetMapping("/insertForm")
    public String insertForm(Model model, HttpSession session){
        QuizDto q = new QuizDto();
        Object email = session.getAttribute("loginEmail");
        if (email != null) q.setWriter(email.toString()); // 표시용
        model.addAttribute("quiz", q);
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

    // ===== 수정/삭제 =====
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

    // ===== 게임 시작/다음 문제 =====
    @GetMapping("/play")
    public String play(Model model, HttpSession session){
        int played = played(session);
        if (played >= MAX_ROUNDS) {
            return "redirect:/quiz/result?finished=1";
        }
        QuizDto q = quizService.pickRandom();
        model.addAttribute("quiz", q);
        model.addAttribute("progress", played + 1); // 이번 문제 번호
        model.addAttribute("max", MAX_ROUNDS);
        return "quiz/play";
    }

    // ===== 정답 체크 =====
    @PostMapping("/check")
    public String check(@RequestParam Long id,
                        @RequestParam String answer,
                        HttpSession session,
                        Model model){
        String email = emailOf(session);
        boolean correct = quizService.checkAnswer(id, answer, email);

        // 라운드 집계 & 진행 카운트
        incRoundBy(session, correct);
        int nowPlayed = played(session) + 1;
        played(session, nowPlayed);

        if (nowPlayed >= MAX_ROUNDS) {
            // 20번째 문제를 풀면 즉시 결과로
            return "redirect:/quiz/result?finished=1";
        }

        model.addAttribute("correct", correct);
        model.addAttribute("progress", nowPlayed);
        model.addAttribute("max", MAX_ROUNDS);
        return "quiz/check";
    }

    // ===== 결과 (이번 라운드 기준 표시, 누적은 참고로 별도 표시) =====
    @GetMapping("/result")
    public String result(HttpSession session, Model model){
        String email = emailOf(session);
        if (email == null) return "redirect:/user/login";

        int rCorrect = roundCorrect(session);
        int rWrong   = roundWrong(session);
        int rTotal   = rCorrect + rWrong;  // ← 이번 라운드 총합 (정확히 20이 되어야 함)

        // 누적(선택 노출용)
        QuizService.UserStats all = quizService.getUserStats(email);

        model.addAttribute("email", email);
        model.addAttribute("played", played(session));
        model.addAttribute("max", MAX_ROUNDS);

        model.addAttribute("rCorrect", rCorrect);
        model.addAttribute("rWrong", rWrong);
        model.addAttribute("rTotal", rTotal);

        model.addAttribute("all", all);
        return "quiz/result";
    }

    // ===== 라운드 리셋 =====
    @GetMapping("/reset")
    public String reset(HttpSession session){
        played(session, 0);
        roundCorrect(session, 0);
        roundWrong(session, 0);
        return "redirect:/quiz/play";
    }
}
