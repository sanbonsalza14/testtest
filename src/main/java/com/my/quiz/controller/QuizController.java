package com.my.quiz.controller;

import com.my.quiz.dto.QuizDto;
import com.my.quiz.repository.UserRepository;
import com.my.quiz.service.QuizService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final UserRepository userRepository; // ✅ 랭킹용 주입

    /** 라운드당 문제 수 */
    private static final int MAX_ROUNDS = 20;

    // ===== 세션 헬퍼 =====
    private String emailOf(HttpSession s){
        Object a = s.getAttribute("loginEmail");
        return (a == null) ? null : a.toString();
    }
    private String nicknameOf(HttpSession s){
        Object a = s.getAttribute("loginNickname");
        return (a == null) ? null : a.toString();
    }
    private boolean isAdmin(HttpSession s){
        Object r = s.getAttribute("role");
        return r != null && "ADMIN".equals(r.toString());
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

    @SuppressWarnings("unchecked")
    private List<Long> roundIds(HttpSession s){
        Object v = s.getAttribute("roundIds");
        return (v instanceof List) ? (List<Long>) v : null;
    }
    private void setRoundIds(HttpSession s, List<Long> ids){ s.setAttribute("roundIds", ids); }

    private void incRoundBy(HttpSession s, boolean correct){
        if (correct) roundCorrect(s, roundCorrect(s) + 1);
        else roundWrong(s, roundWrong(s) + 1);
    }

    // ===== 목록 =====
    @GetMapping
    public String list(Model model, HttpSession session){
        List<QuizDto> list = quizService.findAll();
        model.addAttribute("list", list);
        model.addAttribute("loginEmail", session.getAttribute("loginEmail"));
        model.addAttribute("loginNickname", session.getAttribute("loginNickname"));
        model.addAttribute("role", session.getAttribute("role"));
        return "quiz/showQuiz";
    }

    // ===== 등록 폼 =====
    @GetMapping("/insertForm")
    public String insertForm(Model model, HttpSession session){
        QuizDto q = new QuizDto();
        Object nick = session.getAttribute("loginNickname");
        if (nick != null) q.setWriter(nick.toString());
        model.addAttribute("quiz", q);
        return "quiz/insertForm";
    }

    // ===== 등록 =====
    @PostMapping("/insert")
    public String insert(@Valid @ModelAttribute("quiz") QuizDto dto,
                         BindingResult bindingResult,
                         HttpSession session){
        if (bindingResult.hasErrors()) return "quiz/insertForm";
        String nickname = nicknameOf(session);
        if (nickname == null) return "redirect:/user/login";
        dto.setWriter(nickname);
        quizService.insert(dto);
        return "redirect:/quiz";
    }

    // ===== 수정/삭제 =====
    @GetMapping("/{id}")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session){
        QuizDto dto = quizService.findOne(id);
        if (ObjectUtils.isEmpty(dto)) return "redirect:/quiz";
        String nickname = nicknameOf(session);
        if (!(isAdmin(session) || (nickname != null && quizService.isOwner(dto, nickname)))) {
            return "redirect:/quiz";
        }
        model.addAttribute("quiz", dto);
        return "quiz/updateForm";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("quiz") QuizDto dto,
                         BindingResult bindingResult,
                         HttpSession session){
        if (bindingResult.hasErrors()) return "quiz/updateForm";
        String nickname = nicknameOf(session);
        if (!(isAdmin(session) || (nickname != null && quizService.isOwner(dto.getId(), nickname)))) {
            return "redirect:/quiz";
        }
        quizService.updatePreserveWriter(dto);
        return "redirect:/quiz";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, HttpSession session){
        String nickname = nicknameOf(session);
        if (!(isAdmin(session) || (nickname != null && quizService.isOwner(id, nickname)))) {
            return "redirect:/quiz";
        }
        quizService.delete(id);
        return "redirect:/quiz";
    }

    // ===== 게임 시작/다음 문제 =====
    @GetMapping("/play")
    public String play(Model model, HttpSession session){
        int played = played(session);

        List<Long> ids = roundIds(session);
        if (ids == null || ids.isEmpty()) {
            ids = new ArrayList<>(quizService.sampleRoundIds(MAX_ROUNDS));
            setRoundIds(session, ids);
            played(session, 0);
            roundCorrect(session, 0);
            roundWrong(session, 0);
            played = 0;
        }

        if (played >= ids.size()) {
            return "redirect:/quiz/result?finished=1";
        }

        Long nextId = ids.get(played);
        QuizDto q = quizService.findDtoById(nextId).orElse(null);

        model.addAttribute("quiz", q);
        model.addAttribute("progress", played + 1);
        model.addAttribute("max", ids.size());
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

        incRoundBy(session, correct);
        int nowPlayed = played(session) + 1;
        played(session, nowPlayed);

        List<Long> ids = roundIds(session);
        int max = (ids == null) ? MAX_ROUNDS : ids.size();

        model.addAttribute("correct", correct);
        model.addAttribute("progress", nowPlayed);
        model.addAttribute("max", max);
        return "quiz/check";
    }

    // ===== 결과 + 랭킹 같이 표시 =====
    @GetMapping("/result")
    public String result(HttpSession session, Model model){
        Object nick = session.getAttribute("loginNickname");
        if (nick == null) return "redirect:/user/login";

        int rCorrect = roundCorrect(session);
        int rWrong   = roundWrong(session);
        int rTotal   = rCorrect + rWrong;

        String email = emailOf(session);
        var all = quizService.getUserStats(email);

        List<Long> ids = roundIds(session);
        int max = (ids == null) ? MAX_ROUNDS : ids.size();

        model.addAttribute("email", email);
        model.addAttribute("played", played(session));
        model.addAttribute("max", max);
        model.addAttribute("rCorrect", rCorrect);
        model.addAttribute("rWrong", rWrong);
        model.addAttribute("rTotal", rTotal);
        model.addAttribute("all", all);

        // ✅ 랭킹(승인된 사용자) 상위 10명
        var full = userRepository.findByApprovedTrueOrderByAnswerTrueDescAnswerFalseAscNicknameAsc();
        model.addAttribute("rankTop", (full.size() > 10) ? full.subList(0,10) : full);

        return "quiz/result";
    }

    // ===== 라운드 리셋 =====
    @GetMapping("/reset")
    public String reset(HttpSession session){
        played(session, 0);
        roundCorrect(session, 0);
        roundWrong(session, 0);
        setRoundIds(session, new ArrayList<>());
        return "redirect:/quiz/play";
    }
}
