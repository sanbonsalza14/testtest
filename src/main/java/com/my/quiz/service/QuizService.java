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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final PlayResultRepository playResultRepository;
    private final UserRepository userRepository;

    public List<QuizDto> findAll() {
        return quizRepository.findAll().stream().map(QuizDto::fromEntity).toList();
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

    /** ✅ 라운드 시작 시: 전체 개수와 20 중 작은 값으로 랜덤 ID 묶음을 뽑는다 */
    public List<Long> sampleRoundIds(int maxRoundSize){
        long total = quizRepository.count();
        int limit = (int) Math.min(total, maxRoundSize);
        return quizRepository.pickRandomIds(limit);
    }

    /** ✅ ID로 퀴즈 한 개를 DTO로 */
    public Optional<QuizDto> findDtoById(Long id){
        return quizRepository.findById(id).map(QuizDto::fromEntity);
    }

    /** (기존) 단일 랜덤 - 남겨둠 */
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

    // ...
    public boolean isOwner(Long quizId, String nickname){
        if (nickname == null) return false;
        Quiz q = quizRepository.findById(quizId).orElse(null);
        return q != null && nickname.equals(q.getWriter());
    }

    public boolean isOwner(QuizDto dto, String nickname){
        return dto != null && dto.getWriter() != null && dto.getWriter().equals(nickname);
    }
// ...

    /** ✅ 유저별 정답/오답 집계 */
    public UserStats getUserStats(String email) {
        long correct = playResultRepository.countCorrectByUser(email);
        long wrong   = playResultRepository.countWrongByUser(email);
        return new UserStats(correct, wrong, correct + wrong);
    }

    /** 간단한 통계 DTO (레코드) */
    public record UserStats(long correct, long wrong, long total) {}
}
