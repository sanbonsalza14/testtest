package com.my.quiz.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class UserEntity extends BaseTimeEntity {
    @Id
    private String email;

    @Column(nullable = false)
    private String password;

    /** ✅ 닉네임을 고유값으로 관리 */
    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;  //ADMIN / USER

    @Column(nullable = false)
    private boolean approved = false; //관리자 승인 여부 (status)

    @Column(nullable = false)
    private int answerTrue = 0;  //맞춘수

    @Column(nullable = false)
    private int answerFalse = 0;  //틀린수
}
