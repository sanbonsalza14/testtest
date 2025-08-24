package com.my.quiz.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "quiz")                 // ← 테이블명 소문자로 통일
public class Quiz extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;           // 내용


    @Column(nullable = false, length = 1)
    private String answer;            // "O" / "X"

    @Column(nullable = false, length = 255)
    private String writer;            // ← 소문자 필드명
}