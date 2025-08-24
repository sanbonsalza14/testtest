package com.my.quiz.service;

import com.my.quiz.dto.QuizDto;
import com.my.quiz.entity.PlayResult;
import com.my.quiz.entity.Quiz;
import com.my.quiz.repository.PlayResultRepository;
import com.my.quiz.repository.QuizRepository;
import com.my.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final PlayResultRepository playResultRepository;
    private final UserRepository userRepository;// *주입

    public List<QuizDto> findAll() {
        return quizRepository.findAll()
                .stream().map(QuizDto::fromEntity).toList();
    }

    public QuizDto findOne(Long id) {
        Quiz q = quizRepository.findById(id).orElse(null);
        return ObjectUtils.isEmpty(q) ? null : QuizDto.fromEntity(q);
    }

    public void insert(QuizDto dto) {
        quizRepository.save(QuizDto.toEntity(dto));
    }

    public void updatePreserveWriter(QuizDto dto){
        Quiz origin = quizRepository.findById(dto.getId()).orElse(null);
        if (origin == null) return;
        origin.setContent(dto.getContent());
        origin.setAnswer(dto.getAnswer());
        quizRepository.save(origin);
    }

    public void delete(Long id){
        quizRepository.deleteById(id);
    }

    public QuizDto pickRandom(){
        Quiz q = quizRepository.pickRandomOne();
        return q == null ? null : QuizDto.fromEntity(q);
    }

    public boolean checkAnswer(Long quizId, String userAnswer, String userEmail){
        Quiz q = quizRepository.findById(quizId).orElse(null);
        boolean correct = (q != null) && q.getAnswer().equalsIgnoreCase(userAnswer);
        PlayResult pr = new PlayResult();
        pr.setQuizId(quizId);
        pr.setUserEmail(userEmail == null ? "guest" : userEmail);
        pr.setCorrect(correct);
        pr.setPlayedAt(LocalDateTime.now());
        playResultRepository.save(pr);

        // 회원 카운트 반영(게스트 제외)
        if (userEmail != null) {
            userRepository.findById(userEmail).ifPresent(u -> {
                if (correct) u.setAnswerTrue(u.getAnswerTrue() + 1);
                else u.setAnswerFalse(u.getAnswerFalse() + 1);
                userRepository.save(u);
            });
        }
        return correct;
    }

    public boolean isOwner(Long quizId, String email){
        if (email == null) return false;
        Quiz q = quizRepository.findById(quizId).orElse(null);
        return q != null && email.equals(q.getWriter());
    }

    public boolean isOwner(QuizDto dto, String email){
        return dto != null && dto.getWriter() != null && dto.getWriter().equals(email);
    }
}