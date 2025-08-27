package com.my.quiz.repository;

import com.my.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // 단일 랜덤 문제 1개
    @Query(value = "SELECT * FROM quiz ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Quiz pickRandomOne();

    // 라운드용: 중복 없이 랜덤 ID n개
    @Query(value = "SELECT id FROM quiz ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Long> pickRandomIds(@Param("limit") int limit);

    // 멱등 시딩용
    Optional<Quiz> findByContent(String content);
    boolean existsByContent(String content);
}
