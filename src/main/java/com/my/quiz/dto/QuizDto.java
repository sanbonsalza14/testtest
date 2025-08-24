package com.my.quiz.dto;


import com.my.quiz.entity.Quiz;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @AllArgsConstructor @NoArgsConstructor
public class QuizDto {
    private Long id;


    @NotBlank(message = "퀴즈 내용은 비워둘 수 없습니다.")
    private String content;


    @Pattern(regexp = "^[OX]$", message = "정답은 O 또는 X 만 가능합니다.")
    private String answer; // "O" | "X"


    private String writer; // 서버에서 세션 이메일로 주입


    public static QuizDto fromEntity(Quiz q){
        return new QuizDto(q.getId(), q.getContent(), q.getAnswer(), q.getWriter());
    }
    public static Quiz toEntity(QuizDto dto){
        Quiz q = new Quiz();
        q.setId(dto.getId());
        q.setContent(dto.getContent());
        q.setAnswer(dto.getAnswer());
        q.setWriter(dto.getWriter());
        return q;
    }
}