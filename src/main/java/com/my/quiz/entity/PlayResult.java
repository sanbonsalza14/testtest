package com.my.quiz.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "play_result")
public class PlayResult extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String userEmail;
    @Column(nullable = false)
    private Long quizId;
    @Column(nullable = false)
    private boolean correct;
    @Column(nullable = false)
    private LocalDateTime playedAt;
}