package com.my.quiz.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "quiz")
public class Quiz extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 1)
    private String answer; // "O" / "X"

    /** 작성자 닉네임 컬럼 (DB 컬럼명은 writer_nickname) */
    @Column(name = "writer_nickname", nullable = false, length = 50)
    private String writer;
}
